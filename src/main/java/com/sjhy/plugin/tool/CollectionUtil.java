package com.sjhy.plugin.tool;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * 集合工具类
 *
 * @author makejava
 * @version 1.0.0
 * @since 2018/07/25 10:24
 */
public class CollectionUtil {

    /**
     * 判断集合是否为空的
     *
     * @param collection 集合对象
     * @return 是否为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断map是否为空的
     *
     * @param map map对象
     * @return 是否为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    @NotNull
    public static<T> List<List<T>> splitList(List<T> list, int unitSize) {
        if (list == null || unitSize <= 0) {
            throw new RuntimeException("list is not null and unitSize must be greater than zero");
        }
        List<List<T>> result = new ArrayList<>();
        Queue<T> queue = new LinkedList<>(list);
        double groupNum = Math.ceil(list.size() / (double) unitSize);
        for (int i = 0; i < groupNum; i++) {
            List<T> eachList = new ArrayList<>();
            //last group
            if (i == groupNum - 1) {
                eachList.addAll(queue);
            }
            else {
                for (int j = 0; j < unitSize; j++) {
                    eachList.add(queue.remove());
                }
            }
            result.add(eachList);
        }
        return result;
    }
}
