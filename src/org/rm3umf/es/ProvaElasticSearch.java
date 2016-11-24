package org.rm3umf.es;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.*;
import org.elasticsearch.action.index.IndexResponse;

public class ProvaElasticSearch {
	
	public static void main (String[] args) throws UnknownHostException {
		TransportClient client = TransportClient.builder().build()
		        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("host1"), 9300))
		        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("host2"), 9300));
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("nome", "domenico");
		json.put("cognome", "giammarino");
		IndexResponse r = client.prepareIndex("prova", "prova2").setSource(json).get();
		System.out.println(r.isCreated());
	}

}
