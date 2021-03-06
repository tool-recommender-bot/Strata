/*
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.fxopt;

import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaBean;
import org.joda.beans.MetaProperty;
import org.joda.beans.gen.BeanDefinition;
import org.joda.beans.gen.PropertyDefinition;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;
import org.joda.beans.impl.direct.DirectPrivateBeanBuilder;

import com.opengamma.strata.collect.array.DoubleMatrix;

/**
 * Combines information about a volatility and its sensitivities.
 * <p>
 * This contains a volatility calculated at a particular x and y and the bucketed sensitivities
 * of this value to the volatility data points that were used to construct the surface.
 */
@BeanDefinition(builderScope = "private")
public final class VolatilityAndBucketedSensitivities
    implements ImmutableBean, Serializable {

  /**
   * The volatility.
   */
  @PropertyDefinition
  private final double volatility;
  /**
   * The sensitivities.
   */
  @PropertyDefinition(validate = "notNull")
  private final DoubleMatrix sensitivities;

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance.
   * 
   * @param volatility  the volatility
   * @param sensitivities  the bucketed sensitivities
   * @return the volatility and sensitivities
   */
  public static VolatilityAndBucketedSensitivities of(double volatility, DoubleMatrix sensitivities) {
    return new VolatilityAndBucketedSensitivities(volatility, sensitivities);
  }

  //------------------------- AUTOGENERATED START -------------------------
  /**
   * The meta-bean for {@code VolatilityAndBucketedSensitivities}.
   * @return the meta-bean, not null
   */
  public static VolatilityAndBucketedSensitivities.Meta meta() {
    return VolatilityAndBucketedSensitivities.Meta.INSTANCE;
  }

  static {
    MetaBean.register(VolatilityAndBucketedSensitivities.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  private VolatilityAndBucketedSensitivities(
      double volatility,
      DoubleMatrix sensitivities) {
    JodaBeanUtils.notNull(sensitivities, "sensitivities");
    this.volatility = volatility;
    this.sensitivities = sensitivities;
  }

  @Override
  public VolatilityAndBucketedSensitivities.Meta metaBean() {
    return VolatilityAndBucketedSensitivities.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the volatility.
   * @return the value of the property
   */
  public double getVolatility() {
    return volatility;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the sensitivities.
   * @return the value of the property, not null
   */
  public DoubleMatrix getSensitivities() {
    return sensitivities;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      VolatilityAndBucketedSensitivities other = (VolatilityAndBucketedSensitivities) obj;
      return JodaBeanUtils.equal(volatility, other.volatility) &&
          JodaBeanUtils.equal(sensitivities, other.sensitivities);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(volatility);
    hash = hash * 31 + JodaBeanUtils.hashCode(sensitivities);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("VolatilityAndBucketedSensitivities{");
    buf.append("volatility").append('=').append(volatility).append(',').append(' ');
    buf.append("sensitivities").append('=').append(JodaBeanUtils.toString(sensitivities));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code VolatilityAndBucketedSensitivities}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code volatility} property.
     */
    private final MetaProperty<Double> volatility = DirectMetaProperty.ofImmutable(
        this, "volatility", VolatilityAndBucketedSensitivities.class, Double.TYPE);
    /**
     * The meta-property for the {@code sensitivities} property.
     */
    private final MetaProperty<DoubleMatrix> sensitivities = DirectMetaProperty.ofImmutable(
        this, "sensitivities", VolatilityAndBucketedSensitivities.class, DoubleMatrix.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "volatility",
        "sensitivities");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1917967323:  // volatility
          return volatility;
        case 1226228605:  // sensitivities
          return sensitivities;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends VolatilityAndBucketedSensitivities> builder() {
      return new VolatilityAndBucketedSensitivities.Builder();
    }

    @Override
    public Class<? extends VolatilityAndBucketedSensitivities> beanType() {
      return VolatilityAndBucketedSensitivities.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code volatility} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Double> volatility() {
      return volatility;
    }

    /**
     * The meta-property for the {@code sensitivities} property.
     * @return the meta-property, not null
     */
    public MetaProperty<DoubleMatrix> sensitivities() {
      return sensitivities;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -1917967323:  // volatility
          return ((VolatilityAndBucketedSensitivities) bean).getVolatility();
        case 1226228605:  // sensitivities
          return ((VolatilityAndBucketedSensitivities) bean).getSensitivities();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      metaProperty(propertyName);
      if (quiet) {
        return;
      }
      throw new UnsupportedOperationException("Property cannot be written: " + propertyName);
    }

  }

  //-----------------------------------------------------------------------
  /**
   * The bean-builder for {@code VolatilityAndBucketedSensitivities}.
   */
  private static final class Builder extends DirectPrivateBeanBuilder<VolatilityAndBucketedSensitivities> {

    private double volatility;
    private DoubleMatrix sensitivities;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1917967323:  // volatility
          return volatility;
        case 1226228605:  // sensitivities
          return sensitivities;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case -1917967323:  // volatility
          this.volatility = (Double) newValue;
          break;
        case 1226228605:  // sensitivities
          this.sensitivities = (DoubleMatrix) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public VolatilityAndBucketedSensitivities build() {
      return new VolatilityAndBucketedSensitivities(
          volatility,
          sensitivities);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(96);
      buf.append("VolatilityAndBucketedSensitivities.Builder{");
      buf.append("volatility").append('=').append(JodaBeanUtils.toString(volatility)).append(',').append(' ');
      buf.append("sensitivities").append('=').append(JodaBeanUtils.toString(sensitivities));
      buf.append('}');
      return buf.toString();
    }

  }

  //-------------------------- AUTOGENERATED END --------------------------
}
