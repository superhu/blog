package top.wasm.blog.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import org.springframework.http.*;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Builder
public class RestClient {

    @NonNull
    private HttpMethod method;

    @NonNull
    private String url;

    @Singular("queryParam")
    private Map<String,Object> queryParam;

    @Singular("pathVariable")
    private Map<String,Object> pathVariable;

    private Object body;

    @Singular("header")
    private Map<String,String> header;



    public String execute(){

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        if(!CollectionUtils.isEmpty(header)){
            header.forEach((k,v) ->httpHeaders.set(k,v));
        }
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url);
        if(!CollectionUtils.isEmpty(queryParam)){
            queryParam.forEach((k,v) ->uriBuilder.queryParam(k,v));
        }
        String uri = "";
        if(!CollectionUtils.isEmpty(pathVariable)){
            uri = uriBuilder.buildAndExpand(pathVariable).toUriString();
        }else{
            uri = uriBuilder.build().toUriString();
        }

        HttpEntity<Object> httpEntity = new HttpEntity<>(JSON.toJSONString(body),httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(uri, method, httpEntity, String.class);
        String responseBody = response.getBody();

        return responseBody;
    }

    public <T> T execute2Type(TypeReference<T> type){
        return JSON.parseObject(execute(), type);
    }

}
