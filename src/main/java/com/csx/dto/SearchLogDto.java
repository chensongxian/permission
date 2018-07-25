package com.csx.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

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
public class SearchLogDto {
    private Integer type; // LogType

    private String beforeSeg;

    private String afterSeg;

    private String operator;

    private Date fromTime;//yyyy-MM-dd HH:mm:ss

    private Date toTime; //yyyy-MM-dd HH:mm:ss
}
