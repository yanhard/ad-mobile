package com.admobile.spring.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.admobile.spring.bo.MessageUmbbcBO;
import com.admobile.spring.bo.RequestHttpSiteBO;

@Controller
public class DelayQueueMsisdn {

	private static final Logger log = Logger.getLogger(DelayQueueMsisdn.class);

	private BlockingQueue<DelayObjMsisdn> queue;

	private static BiMap<Integer, String> biMap = HashBiMap.create();

	private static final long minutes = 60000; // 60*1000

	//private final String USER_AGENT = "Mozilla/5.0";

	private long totDelay = 0;

	private boolean runningThread = true;

	private boolean exceedBC = false;

	@Value("${app.hourstart}")
	private int hourstart;

	@Value("${app.hourend}")
	private int hourend;

	@Value("${app.delay}")
	private long delay;

	@Value("${app.url}")
	private String url;

	@Value("${app.sdc}")
	private String sdc;

	@Value("${app.prefixaxis}")
	private String prefixaxis;

	@Autowired
	private MessageUmbbcBO messageUmbbcBO;

	@Autowired
	private RequestHttpSiteBO requestHttpSiteBO;

	public DelayQueueMsisdn() {
		System.out.println("Initiate contructor Delay Queue");
		queue = new DelayQueue<DelayObjMsisdn>();
		start();
	}

	public DelayQueueMsisdn(String msisdn) {
		System.out.println("Process msisdn: " + msisdn);
	}

	@RequestMapping(value = "/")
	@ResponseBody
	private String welcome(){
		return "Welcome to Ad Mobile";
	}

	@RequestMapping(value = "/sendMessage")
	@ResponseBody
	private void sendQueueMsisdn(
		@RequestParam(value = "msisdn") String msisdn) {
		log.debug(msisdn);
		msisdn = checkMsisdn(msisdn);
		String param = msisdn;
		// take hour
		Calendar cal = Calendar.getInstance();
		delayObject(param, delay);
	}

	private void delayObject(String param, long delay) {
		totDelay = delay * minutes;
		DelayObjMsisdn object = new DelayObjMsisdn(param, totDelay);
		try {
			queue.put(object);
			log.debug("Queue size: " + queue.size());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private Thread takeQueueThread = new Thread(new Runnable() {
		int count = 0;
		String param = "";
		String msisdn = "";
		String shortcode = "";
		String msg = "";
		String targetUrl = "";
		String message = "";
		String delims = "[|]";
		String result = "";
		String carrier = "";
		String group = "";

		public void run() {
			// Thread.sleep;
			long start = System.currentTimeMillis();
			while (runningThread && !exceedBC) {
				try {
					// Take elements out from the DelayQueue object.
					DelayObjMsisdn objMsisdn = queue.take();
					//param = objMsisdn.toString();
					//String[] tokens = param.split(delims);
					msisdn = param;//tokens[0];
					//shortcode = tokens[1];
					//group = getGroupMessage(shortcode);

					if (!(msisdn.equals(""))) {
						Calendar cal = Calendar.getInstance();
						int hourNow = cal.get(Calendar.HOUR_OF_DAY);
						if (checkTime(hourNow)) {
							if (mapPutMsisdn(msisdn,group)) {
								try {
									int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
									message = messageUmbbcBO.messageUmbbc(dayOfWeek);
									//	carrier = "xl";
									
									msg = URLEncoder.encode(message, "UTF8");
									targetUrl = url + "?msisdn=" + msisdn
											+ "&sdc=" + sdc + "&message=" + msg;
									// send Url
									result = requestHttpSiteBO
											.getResult(targetUrl);
									log.info(msisdn
											+ "|"
											+ sdc
											+ "|"
											+ dayOfWeek
											+ "|"
											+ message);
								} catch (UnsupportedEncodingException e) {
									log.debug("error encode caused: " + e);
								} catch (Exception ex) {
									log.debug("error url: " + ex);
								}
							}else{
								log.debug("failed check filter or map");
							}
						} else {
							mapClearMsisdn();
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	});

	private void start() {
		this.takeQueueThread.start();
	}

	// check msisdn
	private String checkMsisdn(String msisdn) {
		if (msisdn.startsWith("08")) {
			msisdn = msisdn.replaceFirst("08", "628");
		}
		return msisdn;
	}

	// check unique msisdn in map
	private boolean mapPutMsisdn(String msisdn, String group) {
		// Try to put msisdn on the map
		boolean result = false;
		int keyMap = biMap.size();
		String valueMap = msisdn; //group: ignore
		try {
			biMap.put(keyMap + 1, valueMap);
		} catch (Exception e) {
			log.debug(msisdn + " already on the map");
		}
		if (keyMap < biMap.size())
			result = true;
		return result;
	}

	private void mapClearMsisdn() {
		// try to clear bimap
		if (biMap.size() > 0) {
			log.debug("clear all msisdn at bimap...");
			biMap.clear();
		}
	}
/* 
	private boolean filterBlacklist(String msisdn) {
		// filter blacklist with msisdn
		int count = filterBlacklistBO.getCountMsisdnValue(msisdn);
		if (count > 0) {
			return false;
		} else {
			return true;
		}
	}
*/
	private boolean checkTime(int hour) {
		if (hour >= hourstart && hour <= hourend) {
			return true;
		} else {
			return false;
		}
	}

	private boolean validateInputAxis(String msisdn) {
		boolean isValid = false;

		String prefixlist[] = prefixaxis.split("x");
		for (int a = 0; a < prefixlist.length; a++) {
			if (msisdn.startsWith(prefixlist[a])) {
				isValid = true;
				break;
			}
		}
		return isValid;
	}
/* 	
	private String getGroupMessage(String servicecode){
		String result = "";
		result = messageUmbbcBO.getGroupMessageUmbbc(servicecode);
		return result;
	}
*/	
}