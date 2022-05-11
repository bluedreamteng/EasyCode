package com.sjhy.plugin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intellij.database.model.DasColumn;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.DasUtil;
import com.intellij.psi.PsiClass;
import com.intellij.util.containers.JBIterable;
import com.sjhy.plugin.tool.CollectionUtil;
import com.sjhy.plugin.tool.CurrGroupUtils;
import com.sjhy.plugin.tool.NameUtils;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 表信息
 *
 * @author makejava
 * @version 1.0.0
 * @since 2018/07/17 13:10
 */
@Data
public class TableInfo {

    /**
     * 原始对象
     */
    @JsonIgnore
    private DbTable obj;

    /**
     * 原始对象（从实体生成）
     */
    @JsonIgnore
    private PsiClass psiClassObj;

    /**
     * 表名（首字母大写）
     */
    private String name;
    /**
     * 表名前缀
     */
    private String preName;
    /**
     * 注释
     */
    private String comment;
    /**
     * 模板组名称
     */
    private String templateGroupName;
    /**
     * 所有列
     */
    private List<ColumnInfo> fullColumn;
    /**
     * 主键列
     */
    private List<ColumnInfo> pkColumn;
    /**
     * 其他列
     */
    private List<ColumnInfo> otherColumn;


    private List<ColumnInfo> searchColumn;

    private List<ColumnInfo> listColumn;

    private List<ColumnInfo> editColumn;

    private List<ColumnInfo> detailColumn;


    /**
     * 保存的包名称
     */
    private String savePackageName;
    /**
     * 保存路径
     */
    private String savePath;
    /**
     * 保存的model名称
     */
    private String saveModelName;

    public TableInfo() {
    }

    public TableInfo(@NotNull DbTable dbTable) {
        setObj(dbTable);
        // 设置类名
        setName(NameUtils.getInstance().getClassName(dbTable.getName()));
        // 设置注释
        setComment(dbTable.getComment());
        // 设置所有列
        setFullColumn(new ArrayList<>());
        // 设置主键列
        setPkColumn(new ArrayList<>());
        // 设置其他列
        setOtherColumn(new ArrayList<>());
        // 处理所有列
        JBIterable<? extends DasColumn> columns = DasUtil.getColumns(dbTable);
        for (DasColumn column : columns) {
            ColumnInfo columnInfo = new ColumnInfo();
            // 原始列对象
            columnInfo.setObj(column);
            // 列类型
            columnInfo.setType(getColumnType(column.getDataType().getSpecification()));
            // 短类型
            columnInfo.setShortType(NameUtils.getInstance().getClsNameByFullName(columnInfo.getType()));
            // 列名
            columnInfo.setName(NameUtils.getInstance().getJavaName(column.getName()));
            // 列注释
            columnInfo.setComment(column.getComment());
            // 扩展项
            columnInfo.setExt(new LinkedHashMap<>());
            // 添加到全部列
            getFullColumn().add(columnInfo);
            // 主键列添加到主键列，否则添加到其他列
            if (DasUtil.isPrimary(column)) {
                getPkColumn().add(columnInfo);
            } else {
                getOtherColumn().add(columnInfo);
            }
        }
    }

    public TableInfo(DbTable dbTable, Map<String, ColumnSetting> tableSetting) {
        this(dbTable);
        getFullColumn().forEach(columnInfo -> {
            ColumnSetting columnSetting = tableSetting.get(columnInfo.getObj().getName());
            if(columnSetting != null) {
                columnInfo.setShowList(columnSetting.isShowList());
                columnInfo.setListOrder(columnSetting.getListOrder());
                columnInfo.setShowSearch(columnSetting.isShowSearch());
                columnInfo.setSearchOrder(columnSetting.getSearchOrder());
                columnInfo.setShowEdit(columnSetting.isShowEdit());
                columnInfo.setEditOrder(columnSetting.getEditOrder());
                columnInfo.setShowDetail(columnSetting.isShowDetail());
                columnInfo.setDetailOrder(columnSetting.getDetailOrder());
            } else {
                columnInfo.setShowList(Boolean.FALSE);
                columnInfo.setListOrder(999);
                columnInfo.setShowSearch(Boolean.FALSE);
                columnInfo.setSearchOrder(999);
                columnInfo.setShowEdit(Boolean.FALSE);
                columnInfo.setEditOrder(999);
                columnInfo.setShowDetail(Boolean.FALSE);
                columnInfo.setDetailOrder(999);
            }
        });
        listColumn = getFullColumn().stream().filter(ColumnInfo::isShowList)
                .sorted(Comparator.comparing(ColumnInfo::getListOrder)).collect(Collectors.toList());

        searchColumn = getFullColumn().stream().filter(ColumnInfo::isShowSearch)
                .sorted(Comparator.comparing(ColumnInfo::getSearchOrder)).collect(Collectors.toList());

        editColumn = getFullColumn().stream().filter(ColumnInfo::isShowEdit)
                .sorted(Comparator.comparing(ColumnInfo::getEditOrder)).collect(Collectors.toList());

        detailColumn = getFullColumn().stream().filter(ColumnInfo::isShowDetail)
                .sorted(Comparator.comparing(ColumnInfo::getDetailOrder)).collect(Collectors.toList());
    }

    /**
     * 通过映射获取对应的java类型类型名称
     *
     * @param typeName 列类型
     * @return java类型
     */
    private String getColumnType(String typeName) {
        for (TypeMapper typeMapper : CurrGroupUtils.getCurrTypeMapperGroup().getElementList()) {
            // 不区分大小写进行类型转换
            if (Pattern.compile(typeMapper.getColumnType(), Pattern.CASE_INSENSITIVE).matcher(typeName).matches()) {
                return typeMapper.getJavaType();
            }
        }
        // 没找到直接返回Object
        return "java.lang.Object";
    }

    public List<List<ColumnInfo>> getSearchColumnWithGroup(int unitSize) {
        return CollectionUtil.splitList(searchColumn,unitSize);
    }

    public List<List<ColumnInfo>> getListColumnWithGroup(int unitSize) {
        return CollectionUtil.splitList(listColumn,unitSize);
    }

    public List<List<ColumnInfo>> getEditColumnWithGroup(int unitSize) {
        return CollectionUtil.splitList(editColumn,unitSize);
    }

    public List<List<ColumnInfo>> getDetailColumnWithGroup(int unitSize) {
        return CollectionUtil.splitList(detailColumn,unitSize);
    }
}
