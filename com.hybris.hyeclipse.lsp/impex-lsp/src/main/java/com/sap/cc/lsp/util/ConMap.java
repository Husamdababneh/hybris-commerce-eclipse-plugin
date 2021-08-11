package com.sap.cc.lsp.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class ConMap {

	public static final ConMap INSTANCE = new ConMap();
	
	public final Properties props = new Properties();
	public final Set<String> all;
	public final Map<String, Collection<String>> isAfter;
	public final Map<String, Collection<String>> isFollowedBy;
	public final Map<String, String> type;
	
	private ConMap() {
		InputStream propertiesStream = ConMap.class.getResourceAsStream("/" + ConMap.class.getPackage().getName().replace(".", "/") + "/conSessions.properties");
		try {
			props.load(propertiesStream);
			propertiesStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.all = props.keySet().stream()
			.map(key -> ((String)key).split("\\.")[0])
			.collect(Collectors.toSet());
		this.isAfter = props.entrySet().stream()
			.filter(entry -> ((String)entry.getKey()).endsWith(".isAfter"))
			.collect(Collectors.toMap(
					entry -> ((String)entry.getKey()).split("\\.")[0],
					entry -> Arrays.asList(((String)entry.getValue()).split(","))));
		this.isFollowedBy = new HashMap<String, Collection<String>>();
		this.isAfter.forEach((arrival, starts) -> {
			starts.stream().forEach(start -> {
				Collection<String> arrivals = isFollowedBy.get(start);
				if (arrivals == null) {
					arrivals = new HashSet<>();
				}
				arrivals.add(arrival);
				isFollowedBy.put(start, arrivals);
			});
		});
		this.type = props.entrySet().stream()
			.filter(entry -> ((String)entry.getKey()).endsWith(".difficulty"))
			.collect(Collectors.toMap(
					entry -> ((String)entry.getKey()).split("\\.")[0],
					entry -> (String)entry.getValue()));
	}

	/**
	 * @return the list of routes that link from with to
	 */
	public List<String> findWaysBetween(String from, String to) {
		List<String> res = new ArrayList<String>();
		for (String way : all) {
			if (isAfter.get(way) != null && isAfter.get(way).contains(from) &&
					isFollowedBy.get(way) != null && isFollowedBy.get(way).contains(to)) {
				res.add(way);
			}
		}
		return res;
	}
	
	public boolean startsFrom(String route, String potentialStart) {
		return this.isAfter.get(route) != null && this.isAfter.get(route).contains(potentialStart);
	}
}