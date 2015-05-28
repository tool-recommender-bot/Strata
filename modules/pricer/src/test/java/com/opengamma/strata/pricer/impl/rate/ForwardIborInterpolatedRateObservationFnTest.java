/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.impl.rate;

import static com.opengamma.strata.basics.index.IborIndices.GBP_LIBOR_3M;
import static com.opengamma.strata.basics.index.IborIndices.GBP_LIBOR_6M;
import static com.opengamma.strata.collect.TestHelper.date;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.time.LocalDate;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.opengamma.strata.finance.rate.IborInterpolatedRateObservation;
import com.opengamma.strata.market.sensitivity.IborRateSensitivity;
import com.opengamma.strata.market.sensitivity.PointSensitivities;
import com.opengamma.strata.market.sensitivity.PointSensitivityBuilder;
import com.opengamma.strata.market.value.IborIndexRates;
import com.opengamma.strata.pricer.rate.RatesProvider;

/**
* Test.
*/
@Test
public class ForwardIborInterpolatedRateObservationFnTest {

  private static final LocalDate FIXING_DATE = date(2014, 6, 30);
  private static final LocalDate ACCRUAL_START_DATE = date(2014, 7, 2);
  private static final LocalDate ACCRUAL_END_DATE = date(2014, 11, 2);
  private static final double RATE3 = 0.0123d;
  private static final double RATE6 = 0.0234d;
  private static final IborRateSensitivity SENSITIVITY3 = IborRateSensitivity.of(GBP_LIBOR_3M, FIXING_DATE, 1d);
  private static final IborRateSensitivity SENSITIVITY6 = IborRateSensitivity.of(GBP_LIBOR_6M, FIXING_DATE, 1d);
  private static final double TOLERANCE_RATE = 1.0E-10;

  public void test_rate() {
    RatesProvider mockProv = mock(RatesProvider.class);
    IborIndexRates mockRates3M = mock(IborIndexRates.class);
    IborIndexRates mockRates6M = mock(IborIndexRates.class);
    when(mockProv.iborIndexRates(GBP_LIBOR_3M)).thenReturn(mockRates3M);
    when(mockProv.iborIndexRates(GBP_LIBOR_6M)).thenReturn(mockRates6M);
    when(mockRates3M.rate(FIXING_DATE)).thenReturn(RATE3);
    when(mockRates6M.rate(FIXING_DATE)).thenReturn(RATE6);

    IborInterpolatedRateObservation ro = IborInterpolatedRateObservation.of(GBP_LIBOR_3M, GBP_LIBOR_6M, FIXING_DATE);
    ForwardIborInterpolatedRateObservationFn obs = ForwardIborInterpolatedRateObservationFn.DEFAULT;
    LocalDate fixingStartDate = ro.getShortIndex().calculateEffectiveFromFixing(ro.getFixingDate());
    LocalDate fixingEndDate3M = ro.getShortIndex().calculateMaturityFromEffective(fixingStartDate);
    LocalDate fixingEndDate6M = ro.getLongIndex().calculateMaturityFromEffective(fixingStartDate);
    double days3M = fixingEndDate3M.toEpochDay() - ro.getFixingDate().toEpochDay(); //nb days in 3M fixing period
    double days6M = fixingEndDate6M.toEpochDay() - ro.getFixingDate().toEpochDay(); //nb days in 6M fixing period
    double daysCpn = ACCRUAL_END_DATE.toEpochDay() - ro.getFixingDate().toEpochDay();
    double weight3M = (days6M - daysCpn) / (days6M - days3M);
    double weight6M = (daysCpn - days3M) / (days6M - days3M);
    double rateExpected = (weight3M * RATE3 + weight6M * RATE6);
    double rateComputed = obs.rate(ro, ACCRUAL_START_DATE, ACCRUAL_END_DATE, mockProv);
    assertEquals(rateComputed, rateExpected, TOLERANCE_RATE);
  }

  public void test_rateSensitivity() {
    RatesProvider mockProv = mock(RatesProvider.class);
    IborIndexRates mockRates3M = mock(IborIndexRates.class);
    IborIndexRates mockRates6M = mock(IborIndexRates.class);
    when(mockProv.iborIndexRates(GBP_LIBOR_3M)).thenReturn(mockRates3M);
    when(mockProv.iborIndexRates(GBP_LIBOR_6M)).thenReturn(mockRates6M);
    when(mockRates3M.pointSensitivity(FIXING_DATE)).thenReturn(SENSITIVITY3);
    when(mockRates6M.pointSensitivity(FIXING_DATE)).thenReturn(SENSITIVITY6);

    IborInterpolatedRateObservation ro = IborInterpolatedRateObservation.of(GBP_LIBOR_3M, GBP_LIBOR_6M, FIXING_DATE);
    ForwardIborInterpolatedRateObservationFn obsFn = ForwardIborInterpolatedRateObservationFn.DEFAULT;
    LocalDate fixingStartDate = ro.getShortIndex().calculateEffectiveFromFixing(ro.getFixingDate());
    LocalDate fixingEndDate3M = ro.getShortIndex().calculateMaturityFromEffective(fixingStartDate);
    LocalDate fixingEndDate6M = ro.getLongIndex().calculateMaturityFromEffective(fixingStartDate);
    double days3M = fixingEndDate3M.toEpochDay() - ro.getFixingDate().toEpochDay(); //nb days in 3M fixing period
    double days6M = fixingEndDate6M.toEpochDay() - ro.getFixingDate().toEpochDay(); //nb days in 6M fixing period
    double daysCpn = ACCRUAL_END_DATE.toEpochDay() - ro.getFixingDate().toEpochDay();
    double weight3M = (days6M - daysCpn) / (days6M - days3M);
    double weight6M = (daysCpn - days3M) / (days6M - days3M);
    IborRateSensitivity sens3 = IborRateSensitivity.of(GBP_LIBOR_3M, FIXING_DATE, weight3M);
    IborRateSensitivity sens6 = IborRateSensitivity.of(GBP_LIBOR_6M, FIXING_DATE, weight6M);
    PointSensitivities expected = PointSensitivities.of(ImmutableList.of(sens3, sens6));
    PointSensitivityBuilder test = obsFn.rateSensitivity(ro, ACCRUAL_START_DATE, ACCRUAL_END_DATE, mockProv);
    assertEquals(test.build(), expected);
  }

  public void test_rateSensitivity_finiteDifference() {
    double eps = 1.0e-7;
    RatesProvider mockProv = mock(RatesProvider.class);
    IborIndexRates mockRates3M = mock(IborIndexRates.class);
    IborIndexRates mockRates6M = mock(IborIndexRates.class);
    when(mockProv.iborIndexRates(GBP_LIBOR_3M)).thenReturn(mockRates3M);
    when(mockProv.iborIndexRates(GBP_LIBOR_6M)).thenReturn(mockRates6M);
    when(mockRates3M.rate(FIXING_DATE)).thenReturn(RATE3);
    when(mockRates6M.rate(FIXING_DATE)).thenReturn(RATE6);
    when(mockRates3M.pointSensitivity(FIXING_DATE)).thenReturn(SENSITIVITY3);
    when(mockRates6M.pointSensitivity(FIXING_DATE)).thenReturn(SENSITIVITY6);

    IborInterpolatedRateObservation ro = IborInterpolatedRateObservation.of(GBP_LIBOR_3M, GBP_LIBOR_6M, FIXING_DATE);
    ForwardIborInterpolatedRateObservationFn obs = ForwardIborInterpolatedRateObservationFn.DEFAULT;
    PointSensitivityBuilder test = obs.rateSensitivity(ro, ACCRUAL_START_DATE, ACCRUAL_END_DATE, mockProv);

    IborIndexRates mockRatesUp3M = mock(IborIndexRates.class);
    when(mockRatesUp3M.rate(FIXING_DATE)).thenReturn(RATE3 + eps);
    IborIndexRates mockRatesDw3M = mock(IborIndexRates.class);
    when(mockRatesDw3M.rate(FIXING_DATE)).thenReturn(RATE3 - eps);
    IborIndexRates mockRatesUp6M = mock(IborIndexRates.class);
    when(mockRatesUp6M.rate(FIXING_DATE)).thenReturn(RATE6 + eps);
    IborIndexRates mockRatesDw6M = mock(IborIndexRates.class);
    when(mockRatesDw6M.rate(FIXING_DATE)).thenReturn(RATE6 - eps);

    RatesProvider mockProvUp3M = mock(RatesProvider.class);
    when(mockProvUp3M.iborIndexRates(GBP_LIBOR_3M)).thenReturn(mockRatesUp3M);
    when(mockProvUp3M.iborIndexRates(GBP_LIBOR_6M)).thenReturn(mockRates6M);
    RatesProvider mockProvDw3M = mock(RatesProvider.class);
    when(mockProvDw3M.iborIndexRates(GBP_LIBOR_3M)).thenReturn(mockRatesDw3M);
    when(mockProvDw3M.iborIndexRates(GBP_LIBOR_6M)).thenReturn(mockRates6M);
    RatesProvider mockProvUp6M = mock(RatesProvider.class);
    when(mockProvUp6M.iborIndexRates(GBP_LIBOR_3M)).thenReturn(mockRates3M);
    when(mockProvUp6M.iborIndexRates(GBP_LIBOR_6M)).thenReturn(mockRatesUp6M);
    RatesProvider mockProvDw6M = mock(RatesProvider.class);
    when(mockProvDw6M.iborIndexRates(GBP_LIBOR_3M)).thenReturn(mockRates3M);
    when(mockProvDw6M.iborIndexRates(GBP_LIBOR_6M)).thenReturn(mockRatesDw6M);

    double rateUp3M = obs.rate(ro, ACCRUAL_START_DATE, ACCRUAL_END_DATE, mockProvUp3M);
    double rateDw3M = obs.rate(ro, ACCRUAL_START_DATE, ACCRUAL_END_DATE, mockProvDw3M);
    double senseExpected3M = 0.5 * (rateUp3M - rateDw3M) / eps;

    double rateUp6M = obs.rate(ro, ACCRUAL_START_DATE, ACCRUAL_END_DATE, mockProvUp6M);
    double rateDw6M = obs.rate(ro, ACCRUAL_START_DATE, ACCRUAL_END_DATE, mockProvDw6M);
    double senseExpected6M = 0.5 * (rateUp6M - rateDw6M) / eps;

    assertEquals(test.build().getSensitivities().get(0).getSensitivity(), senseExpected3M, eps);
    assertEquals(test.build().getSensitivities().get(1).getSensitivity(), senseExpected6M, eps);
  }

}