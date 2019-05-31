/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.report;

import com.google.common.collect.ImmutableList;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;

/**
 * 表示业务报表的类。
 * <p>
 * 报表是针对特定目的对计算引擎结果的转换，例如交易列表上的交易报表，或单笔交易上的现金流报表。
 * <p>
 * 报表物理上表示一个带有列标题的数据表。
 */
public interface Report {

  /**
   * 获取驱动报表的结果的评估日期。
   * 
   * @return the valuation date
   */
  public abstract LocalDate getValuationDate();

  /**
   * Gets the instant at which the report was run, which is independent of the valuation date.
   * 
   * @return the run instant
   */
  public abstract Instant getRunInstant();

  /**
   * Gets the number of rows in the report table.
   * 
   * @return the number of rows in the report table
   */
  public abstract int getRowCount();

  /**
   * Gets the report column headers.
   * 
   * @return the column headers
   */
  public abstract ImmutableList<String> getColumnHeaders();

  /**
   * Writes this report out in a CSV format.
   * 
   * @param out  the output stream to write to
   */
  public abstract void writeCsv(OutputStream out);

  /**
   * Writes this report out as an ASCII table.
   * 
   * @param out  the output stream to write to
   */
  public abstract void writeAsciiTable(OutputStream out);

  //-------------------------------------------------------------------------
  /**
   * Gets the number of columns in the report table.
   * 
   * @return the number of columns in the report table
   */
  public default int getColumnCount() {
    return getColumnHeaders().size();
  }

  /**
   * Gets this report as an ASCII table string.
   * 
   * @return the ASCII table string
   */
  public default String toAsciiTableString() {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    writeAsciiTable(os);
    return new String(os.toByteArray(), StandardCharsets.UTF_8);
  }

}
