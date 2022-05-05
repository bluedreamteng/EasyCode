package com.sjhy.plugin.entity;

import lombok.Data;

@Data
public class ColumnSetting {
    private boolean showList;
    private Integer listOrder;
    private boolean showSearch;
    private Integer searchOrder;
    private boolean showEdit;
    private Integer editOrder;
}
