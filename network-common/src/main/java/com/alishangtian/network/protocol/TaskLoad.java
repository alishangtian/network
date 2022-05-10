package com.alishangtian.network.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskLoad {
    private String pluginName;
    private String callbackAddress;
    private String params;
    private String taskId;
    private String groupName;
    private String result;
}
