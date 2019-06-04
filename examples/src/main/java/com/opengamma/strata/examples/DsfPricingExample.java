/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.examples;

import com.google.common.collect.ImmutableList;
import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.basics.StandardId;
import com.opengamma.strata.basics.date.Tenor;
import com.opengamma.strata.calc.CalculationRules;
import com.opengamma.strata.calc.CalculationRunner;
import com.opengamma.strata.calc.Column;
import com.opengamma.strata.calc.Results;
import com.opengamma.strata.calc.runner.CalculationFunctions;
import com.opengamma.strata.data.MarketData;
import com.opengamma.strata.examples.marketdata.ExampleData;
import com.opengamma.strata.examples.marketdata.ExampleMarketData;
import com.opengamma.strata.examples.marketdata.ExampleMarketDataBuilder;
import com.opengamma.strata.measure.Measures;
import com.opengamma.strata.measure.StandardComponents;
import com.opengamma.strata.product.AttributeType;
import com.opengamma.strata.product.SecurityId;
import com.opengamma.strata.product.Trade;
import com.opengamma.strata.product.TradeInfo;
import com.opengamma.strata.product.common.BuySell;
import com.opengamma.strata.product.dsf.Dsf;
import com.opengamma.strata.product.dsf.DsfTrade;
import com.opengamma.strata.product.swap.Swap;
import com.opengamma.strata.product.swap.type.FixedIborSwapConventions;
import com.opengamma.strata.report.ReportCalculationResults;
import com.opengamma.strata.report.trade.TradeReport;
import com.opengamma.strata.report.trade.TradeReportTemplate;

import java.time.LocalDate;
import java.util.List;

/**
 * 使用引擎为可交付交换未来定价的示例(DSF).
 * <p>
 * 这就利用了示例引擎和示例市场数据环境。
 */
public class DsfPricingExample {

    /**
     * 运行该示例，为仪器定价，以ASCII表的形式生成输出。
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        // 设置计算运行程序组件（CalculationRunner）：根据可用处理器数量创建多线程
        try (CalculationRunner runner = CalculationRunner.ofMultiThreaded()) {
            calculate(runner);
        }
    }

    // 获取数据并计算结果网格
    private static void calculate(CalculationRunner runner) {
        // 创建参考数据
        // 使用到的相关配置文件在
        // 1，modules\basics\src\main\resources\META-INF\com\opengamma\strata\config\base\HolidayCalendar.ini
        // 2，modules\basics\src\main\resources\META-INF\com\opengamma\strata\config\base\HolidayCalendarDefaultData.ini
        // 注：CNY = CNBE
        ReferenceData refData = ReferenceData.standard();

        // 创建要计算的交易
        // ImmutableList：不可变集合。ImmutableList.of():添加指定类型为T的元素，这里T为Trade
        // trades有且只有2个元素：Trade对象1==》createTrade1(refData)，Trade对象2==》createTrade2(refData)
        List<Trade> trades = ImmutableList.of(createTrade1(refData), createTrade2(refData));

        // 列，指定要计算的度量值
        List<Column> columns = ImmutableList.of(
                Column.of(Measures.PRESENT_VALUE),
                Column.of(Measures.PV01_CALIBRATED_SUM),
                Column.of(Measures.PV01_CALIBRATED_BUCKETED));

        LocalDate valuationDate = LocalDate.of(2014, 1, 22);
        // 使用内置示例市场数据
        //使用配置文件位置在：examples\src\main\resources\example-marketdata
        //builder()方法会读取该文件夹下的所有相关配置文件（curves，historical-fixings，quotes）
        //参考ExampleMarketDataBuilder类中3个文件夹的位置
        ExampleMarketDataBuilder marketDataBuilder = ExampleMarketData.builder();
        MarketData marketData = marketDataBuilder.buildSnapshot(valuationDate);

        // 计算措施的整套规则
        CalculationFunctions functions = StandardComponents.calculationFunctions();
        //获取规则，参数1：要使用的计算功能；参数2（继承了CalculationParameter接口），控制计算的参数
        //参数2中包含了计算时所需要用的CurveGroup-》RatesCurveGroup（曲线组）
        CalculationRules rules = CalculationRules.of(functions, marketDataBuilder.ratesLookup(valuationDate));

        // 结果中包含了构成表格中所有的数据
        Results results = runner.calculate(rules, trades, columns, marketData, refData);

        // 使用报表运行程序将引擎结果转换为交易报表
        ReportCalculationResults calculationResults =
                ReportCalculationResults.of(valuationDate, trades, columns, results, functions, refData);

        //交易报表模板
        //配置文件位置：examples\src\main\resources\example-reports\cds-report-template.ini
        TradeReportTemplate reportTemplate = ExampleData.loadTradeReportTemplate("dsf-report-template");
        TradeReport tradeReport = TradeReport.of(calculationResults, reportTemplate);
        tradeReport.writeAsciiTable(System.out);
    }

    //-----------------------------------------------------------------------
    // 创建元素Trade1
    private static Trade createTrade1(ReferenceData refData) {
        //Swap，Dsf，Trade都是final类，不能直接通过构造器传参实例化，
        //分别通过各自对应的传参入口，最后再用getProduct()/build()方法得到各自的实例对象

        //获取Swap对象实例
        //第一步：FixedIborSwapConventions.USD_FIXED_6M_LIBOR_3M.createTrade(arg...)-->
        //设置FixedIborSwapConventions规则（约定）：USD_FIXED_6M_LIBOR_3M，创建SwapTrade
        //参数为：交易日期，SwapTrade的期限，买卖状态，名义金额，固定利率，用于解决交易日期的参考数据
        //返回类型SwapTrade
        //第二步：SwapTrade.getProduct()，返回Swap
        //从SwapTrade获取到Swap对象实例
        Swap swap = FixedIborSwapConventions.USD_FIXED_6M_LIBOR_3M.createTrade(
                LocalDate.of(2015, 3, 18), Tenor.TENOR_5Y, BuySell.SELL,
                1, 0.02, refData).getProduct();

        //获取Dsf对象实例
        //第一步：通过内部类Builder向外部类Dsf传参
        //参数分别为：认证标识，最后交易日期，交货日期，合同价值/金额，上面获取到的swap对象
        //第二步：完成传参的Dsf类，通过build()方法创建实体对象
        Dsf product = Dsf.builder()
                .securityId(SecurityId.of("OG-Future", "CME-F1U-Mar15"))
                .lastTradeDate(LocalDate.of(2015, 3, 16))
                .deliveryDate(LocalDate.of(2015, 3, 18))
                .notional(100_000)
                .underlyingSwap(swap)
                .build();

        //DsfTrade是Trade类的间接子类，实例化时提供的参数为：
        //TradeInfo类，Dsf类对象，交易的数量，交易的价格
        return DsfTrade.builder()
                .info(TradeInfo.builder()
                        .id(StandardId.of("example", "1"))
                        .addAttribute(AttributeType.DESCRIPTION, "CME-5Y-DSF Mar15")
                        .counterparty(StandardId.of("mn", "Dealer G"))
                        .tradeDate(LocalDate.of(2015, 3, 18))
                        .settlementDate(LocalDate.of(2015, 3, 18))
                        .build())
                .product(product)
                .quantity(20)
                .price(1.0075)
                .build();
    }

    // 创建元素Trade2，除了数据不同，流程跟Trade1一模一样
    private static Trade createTrade2(ReferenceData refData) {
        Swap swap = FixedIborSwapConventions.USD_FIXED_6M_LIBOR_3M.createTrade(
                LocalDate.of(2015, 6, 17), Tenor.TENOR_5Y, BuySell.SELL,
                1, 0.02, refData).getProduct();

        Dsf product = Dsf.builder()
                .securityId(SecurityId.of("OG-Future", "CME-F1U-Jun15"))
                .lastTradeDate(LocalDate.of(2015, 6, 15))
                .deliveryDate(LocalDate.of(2015, 6, 17))
                .notional(100_000)
                .underlyingSwap(swap)
                .build();

        return DsfTrade.builder()
                .info(TradeInfo.builder()
                        .id(StandardId.of("example", "2"))
                        .addAttribute(AttributeType.DESCRIPTION, "CME-5Y-DSF Jun15")
                        .counterparty(StandardId.of("mn", "Dealer G"))
                        .tradeDate(LocalDate.of(2015, 6, 17))
                        .settlementDate(LocalDate.of(2015, 6, 17))
                        .build())
                .product(product)
                .quantity(20)
                .price(1.0085)
                .build();
    }

}
