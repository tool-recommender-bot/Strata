/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.rate;

import static com.opengamma.strata.basics.currency.Currency.GBP;
import static com.opengamma.strata.basics.currency.Currency.USD;
import static com.opengamma.strata.basics.date.DayCounts.ACT_365F;
import static com.opengamma.strata.basics.date.DayCounts.ACT_ACT_ISDA;
import static com.opengamma.strata.basics.index.FxIndices.WM_GBP_USD;
import static com.opengamma.strata.basics.index.IborIndices.USD_LIBOR_3M;
import static com.opengamma.strata.basics.index.OvernightIndices.USD_FED_FUND;
import static com.opengamma.strata.collect.TestHelper.assertThrowsIllegalArg;
import static com.opengamma.strata.collect.TestHelper.coverBeanEquals;
import static com.opengamma.strata.collect.TestHelper.coverImmutableBean;
import static org.testng.Assert.assertEquals;

import java.time.LocalDate;
import java.time.YearMonth;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.opengamma.analytics.financial.model.interestrate.curve.YieldAndDiscountCurve;
import com.opengamma.analytics.financial.model.interestrate.curve.YieldCurve;
import com.opengamma.analytics.math.curve.ConstantDoublesCurve;
import com.opengamma.strata.basics.currency.CurrencyPair;
import com.opengamma.strata.basics.currency.FxMatrix;
import com.opengamma.strata.collect.timeseries.LocalDateDoubleTimeSeries;

/**
 * Test {@link ImmutableRatesProvider}.
 */
@Test
public class ImmutableRatesProviderTest {

  private static final LocalDate PREV_DATE = LocalDate.of(2014, 6, 27);
  private static final LocalDate VAL_DATE = LocalDate.of(2014, 6, 30);
  private static final LocalDate NEXT_DATE = LocalDate.of(2014, 7, 1);
  private static final double FX_GBP_USD = 1.6d;
  private static final FxMatrix FX_MATRIX = FxMatrix.of(GBP, USD, FX_GBP_USD);

  private static final double GBP_DSC = 0.99d;
  private static final double USD_DSC = 0.95d;
  private static final YieldAndDiscountCurve DISCOUNT_CURVE_GBP =
      new YieldCurve("GBP-Discount", new ConstantDoublesCurve(GBP_DSC)) {
        @Override
        public double getDiscountFactor(double t) {
          return GBP_DSC;
        }
      };
  private static final YieldAndDiscountCurve DISCOUNT_CURVE_USD =
      new YieldCurve("USD-Discount", new ConstantDoublesCurve(USD_DSC)) {
        @Override
        public double getDiscountFactor(double t) {
          return USD_DSC;
        }
      };
  private static final YieldAndDiscountCurve USD_LIBOR_CURVE =
      new YieldCurve("USD-LIBOR-3M", new ConstantDoublesCurve(0.97d)) {
        @Override
        public double getDiscountFactor(double t) {
          // reverse engineer discount factor from desired interest rate
          LocalDate fixingStartDate = USD_LIBOR_3M.calculateEffectiveFromFixing(NEXT_DATE);
          double time = USD_LIBOR_3M.getDayCount().relativeYearFraction(VAL_DATE, fixingStartDate);
          if (t >= 0 && t <= time) {
            LocalDate fixingEndDate = USD_LIBOR_3M.calculateMaturityFromEffective(fixingStartDate);
            double fixingYearFraction = USD_LIBOR_3M.getDayCount().yearFraction(fixingStartDate, fixingEndDate);
            return (0.0123d * fixingYearFraction + 1) * 0.96d;
          }
          return 0.96d;
        }
      };
  private static final YieldAndDiscountCurve FED_FUND_CURVE =
      new YieldCurve("USD-FED-FUND", new ConstantDoublesCurve(0.97d)) {
        @Override
        public double getDiscountFactor(double t) {
          throw new UnsupportedOperationException();
        }
      };

  //-------------------------------------------------------------------------
  public void test_builder() {
    LocalDateDoubleTimeSeries ts = LocalDateDoubleTimeSeries.of(PREV_DATE, 0.62d);
    ImmutableRatesProvider test = ImmutableRatesProvider.builder()
        .valuationDate(VAL_DATE)
        .timeSeries(ImmutableMap.of(WM_GBP_USD, ts))
        .dayCount(ACT_ACT_ISDA)
        .build();
    assertEquals(test.getValuationDate(), VAL_DATE);
    assertEquals(ImmutableRatesProvider.meta().timeSeries().get(test), ImmutableMap.of(WM_GBP_USD, ts));
    assertEquals(ImmutableRatesProvider.meta().dayCount().get(test), ACT_ACT_ISDA);
  }

  public void test_builder_invalidAdditionalData() {
    assertThrowsIllegalArg(() -> ImmutableRatesProvider.builder()
        .valuationDate(VAL_DATE)
        .additionalData(ImmutableMap.of(String.class, YearMonth.now()))
        .dayCount(ACT_ACT_ISDA)
        .build());
  }

  //-------------------------------------------------------------------------
  public void test_data() {
    YearMonth sample = YearMonth.now();
    ImmutableRatesProvider test = ImmutableRatesProvider.builder()
        .valuationDate(VAL_DATE)
        .additionalData(ImmutableMap.of(YearMonth.class, sample))
        .dayCount(ACT_ACT_ISDA)
        .build();
    assertEquals(test.data(YearMonth.class), sample);
    assertThrowsIllegalArg(() -> test.data(String.class));
  }

  //-------------------------------------------------------------------------
  public void test_discountFactors() {
    ImmutableRatesProvider test = ImmutableRatesProvider.builder()
        .valuationDate(VAL_DATE)
        .discountCurves(ImmutableMap.of(GBP, DISCOUNT_CURVE_GBP, USD, DISCOUNT_CURVE_USD))
        .dayCount(ACT_ACT_ISDA)
        .build();
    assertEquals(test.discountFactors(GBP).getCurrency(), GBP);
  }

  //-------------------------------------------------------------------------
  public void test_discountFactor() {
    ImmutableRatesProvider test = ImmutableRatesProvider.builder()
        .valuationDate(VAL_DATE)
        .discountCurves(ImmutableMap.of(GBP, DISCOUNT_CURVE_GBP))
        .dayCount(ACT_ACT_ISDA)
        .build();
    assertEquals(test.discountFactor(GBP, LocalDate.of(2014, 7, 30)), GBP_DSC, 0d);
  }

  public void test_discountFactor_notKnown() {
    ImmutableRatesProvider test = ImmutableRatesProvider.builder()
        .valuationDate(VAL_DATE)
        .dayCount(ACT_ACT_ISDA)
        .build();
    assertThrowsIllegalArg(() -> test.discountFactor(GBP, LocalDate.of(2014, 7, 30)));
  }

  //-------------------------------------------------------------------------
  public void test_fxRate_separate() {
    ImmutableRatesProvider test = ImmutableRatesProvider.builder()
        .valuationDate(VAL_DATE)
        .fxMatrix(FX_MATRIX)
        .dayCount(ACT_ACT_ISDA)
        .build();
    assertEquals(test.fxRate(USD, GBP), 1 / FX_GBP_USD, 0d);
    assertEquals(test.fxRate(USD, USD), 1d, 0d);
  }

  public void test_fxRate_pair() {
    ImmutableRatesProvider test = ImmutableRatesProvider.builder()
        .valuationDate(VAL_DATE)
        .fxMatrix(FX_MATRIX)
        .dayCount(ACT_ACT_ISDA)
        .build();
    assertEquals(test.fxRate(CurrencyPair.of(USD, GBP)), 1 / FX_GBP_USD, 0d);
  }

  //-------------------------------------------------------------------------
  public void test_fxIndexRates() {
    LocalDateDoubleTimeSeries ts = LocalDateDoubleTimeSeries.of(VAL_DATE, 0.62d);
    ImmutableRatesProvider test = ImmutableRatesProvider.builder()
        .valuationDate(VAL_DATE)
        .fxMatrix(FX_MATRIX)
        .discountCurves(ImmutableMap.of(GBP, DISCOUNT_CURVE_GBP, USD, DISCOUNT_CURVE_USD))
        .timeSeries(ImmutableMap.of(WM_GBP_USD, ts))
        .dayCount(ACT_ACT_ISDA)
        .build();
    assertEquals(test.fxIndexRates(WM_GBP_USD).getIndex(), WM_GBP_USD);
    assertEquals(test.fxIndexRates(WM_GBP_USD).getTimeSeries(), ts);
  }

  //-------------------------------------------------------------------------
  public void test_iborIndexRates() {
    LocalDateDoubleTimeSeries ts = LocalDateDoubleTimeSeries.of(VAL_DATE, 0.62d);
    ImmutableRatesProvider test = ImmutableRatesProvider.builder()
        .valuationDate(VAL_DATE)
        .indexCurves(ImmutableMap.of(USD_LIBOR_3M, USD_LIBOR_CURVE))
        .timeSeries(ImmutableMap.of(USD_LIBOR_3M, ts))
        .dayCount(ACT_ACT_ISDA)
        .build();
    assertEquals(test.iborIndexRates(USD_LIBOR_3M).getIndex(), USD_LIBOR_3M);
    assertEquals(test.iborIndexRates(USD_LIBOR_3M).getTimeSeries(), ts);
  }

  //-------------------------------------------------------------------------
  public void test_overnightIndexRates() {
    LocalDateDoubleTimeSeries ts = LocalDateDoubleTimeSeries.of(VAL_DATE, 0.62d);
    ImmutableRatesProvider test = ImmutableRatesProvider.builder()
        .valuationDate(VAL_DATE)
        .indexCurves(ImmutableMap.of(USD_FED_FUND, FED_FUND_CURVE))
        .timeSeries(ImmutableMap.of(USD_FED_FUND, ts))
        .dayCount(ACT_ACT_ISDA)
        .build();
    assertEquals(test.overnightIndexRates(USD_FED_FUND).getIndex(), USD_FED_FUND);
    assertEquals(test.overnightIndexRates(USD_FED_FUND).getTimeSeries(), ts);
  }

  //-------------------------------------------------------------------------
  public void test_relativeTime() {
    ImmutableRatesProvider test = ImmutableRatesProvider.builder()
        .valuationDate(VAL_DATE)
        .dayCount(ACT_ACT_ISDA)
        .build();
    assertEquals(test.relativeTime(LocalDate.of(2014, 7, 30)),
        ACT_ACT_ISDA.yearFraction(VAL_DATE, LocalDate.of(2014, 7, 30)), 0d);
    assertEquals(test.relativeTime(LocalDate.of(2014, 5, 30)),
        -ACT_ACT_ISDA.yearFraction(LocalDate.of(2014, 5, 30), VAL_DATE), 0d);
  }

  //-------------------------------------------------------------------------
  public void coverage() {
    ImmutableRatesProvider test = ImmutableRatesProvider.builder()
        .valuationDate(VAL_DATE)
        .dayCount(ACT_ACT_ISDA)
        .build();
    coverImmutableBean(test);
    ImmutableRatesProvider test2 = ImmutableRatesProvider.builder()
        .valuationDate(LocalDate.of(2014, 6, 27))
        .discountCurves(ImmutableMap.of(GBP, DISCOUNT_CURVE_GBP))
        .timeSeries(ImmutableMap.of(USD_LIBOR_3M, LocalDateDoubleTimeSeries.empty()))
        .dayCount(ACT_365F)
        .build();
    coverBeanEquals(test, test2);
  }

}