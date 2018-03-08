package cn.bluesking.api.manager.helper;

import cn.bluesking.api.manager.core.Iterator;

/**
 * 迭代器助手类
 * 
 * @author 随心
 *
 */
public final class IteratorHelper {

    /** 摘要截取范围,当前字符前后半径 */
    private static final int SUMMARY_RANGE = 20;
    
    /**
     * 当前摘要,输出迭代器当前游标位置附近的字符,一般用来输出错误信息
     * <pre>
     * 当前游标如果已经越界了,只会输出附近不会越界部分数据,
     * 如果游标及其附近区域全部都越界了,则只会输出当前游标值
     * </pre>
     * 
     * @param iterator [Iterator<Character>]字符数组迭代器
     * @return
     */
    public static String currentSummary(Iterator<Character> iterator) {
        return currentSummary(iterator, iterator.getCursor());
    }
    
    /**
     * 指定游标摘要,输出迭代器当前游标位置附近的字符,一般用来输出错误信息
     * <pre>
     * 如果游标已经越界了,只会输出附近不会越界部分数据,
     * 如果游标及其附近区域全部都越界了,则只会输出当前游标值
     * </pre>
     * 
     * @param iterator [Iterator<Character>]字符数组迭代器
     * @return
     */
    public static String currentSummary(Iterator<Character> iterator, int cursor) {
        StringBuilder buf = new StringBuilder("\n");
        int maxCursor = cursor + SUMMARY_RANGE;
        int length = iterator.length();
        for (int i = cursor - SUMMARY_RANGE; i <= maxCursor; i ++) {
            if (i >= 0 && i < length) {
                if (i == cursor) {
                    buf.append('【').append(iterator.getElement(i)).append('】');
                } else {
                    buf.append(iterator.getElement(i));
                }
            }
        }
        buf.append("\n在第" + (cursor + 1) + "个字符（已用【】符号标识出来）附近!\n");
        return buf.toString();
    }
    
}
