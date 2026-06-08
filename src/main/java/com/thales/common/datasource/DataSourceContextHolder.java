package com.thales.common.datasource;

import java.util.ArrayDeque;
import java.util.Deque;

public final class DataSourceContextHolder {

    private static final ThreadLocal<Deque<DataSourceType>> CONTEXT =
            ThreadLocal.withInitial(ArrayDeque::new);

    private DataSourceContextHolder() {}

    public static void push(DataSourceType type) {
        CONTEXT.get().push(type);
    }

    public static void pop() {
        Deque<DataSourceType> stack = CONTEXT.get();
        stack.poll();
        if (stack.isEmpty()) {
            CONTEXT.remove();
        }
    }

    public static DataSourceType current() {
        Deque<DataSourceType> stack = CONTEXT.get();
        return stack.isEmpty() ? DataSourceType.PRIMARY : stack.peek();
    }
}
