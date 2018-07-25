package com.csx.beans;

import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

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
@Builder
public class PageResult<T> {
    private List<T> data = Lists.newArrayList();

    private int total = 0;
}
