package org.psd.common;

import lombok.Data;

import java.util.List;

/**
 * 封装Redis查询分页信息
 *
 * @author pengshidun
 */
@Data
public class PageInfo<T> {
    /**
     * 分页开始索引
     */
    private Integer startIndex;
    /**
     * 分页结束索引
     */
    private Integer endIndex;
    /**
     * 每页显示个数
     */
    private Integer pageSize;
    /**
     * 当前在第几页
     */
    private Integer pageNum;
    /**
     * 总页数
     */
    private Integer totalPage;
    /**
     * 总记录数
     */
    private Long totalCount;
    /**
     * 上一页
     */
    private Integer prePageNum;
    /**
     * 下一页
     */
    private Integer nextPageNum;
    /**
     * 分页地址
     */
    private String url;
    /**
     * 待显示的数据
     */
    private List<T> records;

    /**
     * 构造方法，初始化分页信息
     *
     * @param pageNum    当前页码
     * @param pageSize   每页显示个数
     * @param totalCount 总记录数
     */
    public PageInfo(Integer pageNum, Integer pageSize, Long totalCount) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        init();
    }

    /**
     * 初始化分页详细，包括 总页数、前一页页码、下一页页码
     *
     * @author pengshidun
     */
    private void init() {
        // 处理非法参数并设置默认值
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }
        // 计算分页开始索引
        startIndex = (pageNum - 1) * pageSize;
        // 计算分页结束索引
        endIndex = startIndex + pageSize - 1;
        // 计算总页数
        totalPage = Math.toIntExact(totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1);
        // 计算前一页页码
        prePageNum = Math.max(pageNum - 1, 1);
        // 计算下一页页码
        nextPageNum = Math.min(pageNum + 1, totalPage);
    }
}
