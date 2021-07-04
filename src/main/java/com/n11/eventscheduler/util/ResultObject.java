package com.n11.eventscheduler.util;

import com.n11.eventscheduler.enums.ResultEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cihan.bulut on 7/1/2021
 */

public class ResultObject {

    private Map<ResultEnum,Object> resultMap = new HashMap<>();

    public ResultObject(){

    }

    public ResultObject(ResultEnum resultCode, Object o) {
        this.resultMap.put(resultCode,o);
    }

    public void fillResult(ResultEnum resultCode, Object o){
        resultMap.put(resultCode,o);
    }

    public Object fetchAndRemove(ResultEnum resultCode){
        if(resultMap.containsKey(resultCode)) {
            return resultMap.remove(resultCode);
        }else {
            return null;
        }

    }

    public void clearResulMap(){
        resultMap.clear();
    }


}
