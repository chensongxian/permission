package com.csx.param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description: TODO
 * @author: csx
 * @Date: 2018-07-25
 */

@Getter
@Setter
@ToString
public class SearchLogParam {
    private Integer type; // LogType

    private String beforeSeg;

    private String afterSeg;

    private String operator;

    private String fromTime;//yyyy-MM-dd HH:mm:ss

    private String toTime;
}
