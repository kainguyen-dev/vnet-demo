package com.platform.vnetdemo.properties;

import lombok.Data;

@Data
public class StreamProperties {

    String topic;
    String groupKey;
    String[] sumField;

}
