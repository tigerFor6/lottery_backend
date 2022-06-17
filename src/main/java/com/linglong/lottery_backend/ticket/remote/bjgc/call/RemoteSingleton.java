package com.linglong.lottery_backend.ticket.remote.bjgc.call;//package com.linglong.lottery_backend.ticket.bean.bjgc.call;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.web.client.RestTemplate;
//
///**
// * hua
// */
//public final class RemoteSingleton {
//
//    @Autowired
//    RestTemplate restTemplate;
//
//    private RemoteSingleton() {
//        if (SingletonHolder.instance != null) {
//            throw new IllegalStateException();
//        }
//    }
//
//    private static class SingletonHolder {
//        private static RemoteSingleton instance = new RemoteSingleton();
//    }
//
//    public static RemoteSingleton getInstance() {
//        return SingletonHolder.instance;
//    }
//
//    public String postXmlForEntity(String body){
//        HttpHeaders headers = new HttpHeaders();
//        MediaType type = MediaType.parseMediaType("application/xml; charset=UTF-8");
//        headers.setContentType(type);
//
//        HttpEntity<String> formEntity = new HttpEntity<String>(body, headers);
//
//        String result = restTemplate.postForObject("http://60.205.176.126:8080/jc/jc/bet", formEntity, String.class);
//        System.out.println(result);
//        return result;
//    }
//}
