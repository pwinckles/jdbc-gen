package com.pwinckles.jdbcgen;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface JdbcGenDb<E, I, P extends BasePatch, C> {

    // TODO javadoc

    E select(I id, Connection conn) throws SQLException;

    List<E> selectAll(Connection conn) throws SQLException;

    List<E> selectAll(C orderBy, OrderDirection direction, Connection conn) throws SQLException;

    long count(Connection conn) throws SQLException;

    I insert(E entity, Connection conn) throws SQLException;

    I insert(P entity, Connection conn) throws SQLException;

    // TODO note that all ids must be specified or absent
    List<I> insert(List<E> entities, Connection conn) throws SQLException;

    int update(E entity, Connection conn) throws SQLException;

    int update(I id, P entity, Connection conn) throws SQLException;

    int[] update(List<E> entities, Connection conn) throws SQLException;

    int delete(I id, Connection conn) throws SQLException;

    int[] delete(List<I> ids, Connection conn) throws SQLException;

    int deleteAll(Connection conn) throws SQLException;

}
