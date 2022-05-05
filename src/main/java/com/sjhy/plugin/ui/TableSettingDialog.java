package com.sjhy.plugin.ui;

import com.intellij.database.psi.DbTable;
import com.sjhy.plugin.entity.ColumnSetting;
import com.sjhy.plugin.entity.TableInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableSettingDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTable table;
    private final DbTable dbTable;
    private final Map<String, ColumnSetting> tableSetting = new HashMap<>();

    public TableSettingDialog(DbTable dbTable) {
        this.dbTable = dbTable;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        initColumn();
    }

    private void initColumn() {
        DefaultTableModel tableModel = new TableModel();
        table.setModel(tableModel);
        table.setRowSelectionAllowed(false);
        table.setRowSelectionAllowed(false);

//        table.getColumn("列表").setCellEditor(new BooleanTableCellEditor());
//        table.getColumn("搜索").setCellEditor(new BooleanTableCellEditor());
//        table.getColumn("编辑").setCellEditor(new BooleanTableCellEditor());
    }

    private class TableModel extends DefaultTableModel {
        Class<?>[] types =  {String.class,String.class,Boolean.class,Integer.class,Boolean.class,Integer.class,Boolean.class,Integer.class,Boolean.class,Integer.class};

        public TableModel() {
            addColumn("名称");
            addColumn("备注");
            addColumn("列表");
            addColumn("列表排序");
            addColumn("搜索");
            addColumn("搜索排序");
            addColumn("编辑");
            addColumn("编辑排序");
            addColumn("详情");
            addColumn("详情排序");
            TableInfo tableInfo = new TableInfo(dbTable);
            tableInfo.getFullColumn().forEach(columnInfo -> {
                List<Object> dataList = new ArrayList<>();
                dataList.add(columnInfo.getName());
                dataList.add(columnInfo.getComment());
                dataList.add(Boolean.FALSE);
                dataList.add(999);
                dataList.add(Boolean.FALSE);
                dataList.add(999);
                dataList.add(Boolean.FALSE);
                dataList.add(999);
                dataList.add(Boolean.FALSE);
                dataList.add(999);
                addRow(dataList.toArray());
            });
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return types[columnIndex];
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            if(column < 2) {
                return false;
            }
            return super.isCellEditable(row, column);
        }
    }

    private void onOK() {
        // add your code here
        int rowCount = table.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            String name = (String)table.getModel().getValueAt(i,0);
            ColumnSetting columnSetting = new ColumnSetting();
            columnSetting.setShowList((boolean)table.getModel().getValueAt(i,2));
            columnSetting.setListOrder((Integer) table.getModel().getValueAt(i,3));
            columnSetting.setShowSearch((boolean) table.getModel().getValueAt(i,4));
            columnSetting.setSearchOrder((Integer) table.getModel().getValueAt(i,5));
            columnSetting.setShowEdit((boolean) table.getModel().getValueAt(i,6));
            columnSetting.setEditOrder((Integer) table.getModel().getValueAt(i,7));
            columnSetting.setShowDetail((boolean) table.getModel().getValueAt(i,8));
            columnSetting.setDetailOrder((Integer) table.getModel().getValueAt(i,9));
            tableSetting.put(name,columnSetting);
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public Map<String, ColumnSetting> getTableSetting() {
        return tableSetting;
    }

    public void open() {
        setTitle("Table Setting " + dbTable.getName());
        setMinimumSize(new Dimension(1000,1000/16*9));
//        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
