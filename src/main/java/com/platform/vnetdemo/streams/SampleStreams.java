package com.platform.vnetdemo.streams;

import com.platform.vnetdemo.properties.PlatformProperties;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component
public class SampleStreams implements DisposableBean {
    public SampleStreams(PlatformProperties properties) {


    }


    @Override
    public void destroy() throws Exception {
        System.out.println("Destroying steam ...");
    }
}
