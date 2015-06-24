/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.finance.credit.type;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.strata.basics.BuySell;
import com.opengamma.strata.collect.id.StandardId;
import com.opengamma.strata.finance.Template;
import com.opengamma.strata.finance.credit.CdsTrade;
import com.opengamma.strata.finance.credit.RestructuringClause;
import com.opengamma.strata.finance.credit.SeniorityLevel;

/**
 * Template that, when used in conjunction with a {@code CdsConvention} allows
 * standard {@link CdsTrade} objects to be created.
 */
@BeanDefinition
public final class CdsTemplate
    implements Template, ImmutableBean, Serializable {
  // TODO: template should correspond to something that can be quoted for
  // as defined, this adds nothing above the convention

  /**
   * The market convention of the swap.
   */
  @PropertyDefinition(validate = "notNull")
  private final CdsConvention convention;

  //-------------------------------------------------------------------------
  /**
   * Creates a single-name CDS trade with no upfront fee.
   * 
   * @param startDate  the date that the CDS starts
   * @param endDate  the date that the CDS ends
   * @param buySell  whether protection is being bought or sold
   * @param notional  the notional amount
   * @param coupon  the coupon amount
   * @param referenceEntityId  the identifier of the reference entity
   * @param seniorityLevel  the seniority level
   * @param restructuringClause  the restructuring clause
   * @return the single-name CDS
   */
  public CdsTrade toSingleNameTrade(
      LocalDate startDate,
      LocalDate endDate,
      BuySell buySell,
      double notional,
      double coupon,
      StandardId referenceEntityId,
      SeniorityLevel seniorityLevel,
      RestructuringClause restructuringClause) {
    // TODO: need optional fee, not LocalDate.MIN

    return convention.toSingleNameTrade(
        startDate,
        endDate,
        buySell,
        notional,
        coupon,
        referenceEntityId,
        seniorityLevel,
        restructuringClause,
        0d,
        LocalDate.MIN);
  }

  /**
   * Creates a single-name CDS with an upfront fee.
   * 
   * @param startDate  the date that the CDS starts
   * @param endDate  the date that the CDS ends
   * @param buySell  whether protection is being bought or sold
   * @param notional  the notional amount
   * @param coupon  the coupon amount
   * @param referenceEntityId  the identifier of the reference entity
   * @param seniorityLevel  the seniority level
   * @param restructuringClause  the restructuring clause
   * @param upfrontFeeAmount  the amount of the upfront fee
   * @param upfrontFeePaymentDate  the payment date of the upfront fee
   * @return the single-name CDS
   */
  public CdsTrade toSingleNameTrade(
      LocalDate startDate,
      LocalDate endDate,
      BuySell buySell,
      double notional,
      double coupon,
      StandardId referenceEntityId,
      SeniorityLevel seniorityLevel,
      RestructuringClause restructuringClause,
      double upfrontFeeAmount,
      LocalDate upfrontFeePaymentDate) {

    return convention.toSingleNameTrade(
        startDate,
        endDate,
        buySell,
        notional,
        coupon,
        referenceEntityId,
        seniorityLevel,
        restructuringClause,
        upfrontFeeAmount,
        upfrontFeePaymentDate);
  }

  //-------------------------------------------------------------------------
  /**
   * Creates an index CDS with no upfront fee.
   * 
   * @param startDate  the date that the CDS starts
   * @param endDate  the date that the CDS ends
   * @param buySell  whether protection is being bought or sold
   * @param notional  the notional amount
   * @param coupon  the coupon amount
   * @param indexId  the identifier of the index
   * @param indexSeries  the index series
   * @param indexAnnexVersion  the index annex version
   * @return the index CDS trade
   */
  public CdsTrade toIndexTrade(
      LocalDate startDate,
      LocalDate endDate,
      BuySell buySell,
      double notional,
      double coupon,
      StandardId indexId,
      int indexSeries,
      int indexAnnexVersion) {
    // TODO: need optional fee, not LocalDate.MIN

    return convention.toIndexTrade(
        startDate,
        endDate,
        buySell,
        notional,
        coupon,
        indexId,
        indexSeries,
        indexAnnexVersion,
        0d,
        LocalDate.MIN);
  }

  /**
   * Creates an index CDS with an upfront fee
   * 
   * @param startDate  the date that the CDS starts
   * @param endDate  the date that the CDS ends
   * @param buySell  whether protection is being bought or sold
   * @param notional  the notional amount
   * @param coupon  the coupon amount
   * @param indexId  the identifier of the index
   * @param indexSeries  the index series
   * @param indexAnnexVersion  the index annex version
   * @param upfrontFeeAmount  the amount of the upfront fee
   * @param upfrontFeePaymentDate  the payment date of the upfront fee
   * @return the index CDS trade
   */
  public CdsTrade toIndexTrade(
      LocalDate startDate,
      LocalDate endDate,
      BuySell buySell,
      double notional,
      double coupon,
      StandardId indexId,
      int indexSeries,
      int indexAnnexVersion,
      double upfrontFeeAmount,
      LocalDate upfrontFeePaymentDate) {

    return convention.toIndexTrade(
        startDate,
        endDate,
        buySell,
        notional,
        coupon,
        indexId,
        indexSeries,
        indexAnnexVersion,
        upfrontFeeAmount,
        upfrontFeePaymentDate);
  }

  /**
   * Creates a CDS template from a CDS convention.
   * 
   * @param convention  the CDS convention
   * @return the CDS template
   */
  public static CdsTemplate of(CdsConvention convention) {
    return new CdsTemplate(convention);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code CdsTemplate}.
   * @return the meta-bean, not null
   */
  public static CdsTemplate.Meta meta() {
    return CdsTemplate.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(CdsTemplate.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static CdsTemplate.Builder builder() {
    return new CdsTemplate.Builder();
  }

  private CdsTemplate(
      CdsConvention convention) {
    JodaBeanUtils.notNull(convention, "convention");
    this.convention = convention;
  }

  @Override
  public CdsTemplate.Meta metaBean() {
    return CdsTemplate.Meta.INSTANCE;
  }

  @Override
  public <R> Property<R> property(String propertyName) {
    return metaBean().<R>metaProperty(propertyName).createProperty(this);
  }

  @Override
  public Set<String> propertyNames() {
    return metaBean().metaPropertyMap().keySet();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the market convention of the swap.
   * @return the value of the property, not null
   */
  public CdsConvention getConvention() {
    return convention;
  }

  //-----------------------------------------------------------------------
  /**
   * Returns a builder that allows this bean to be mutated.
   * @return the mutable builder, not null
   */
  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      CdsTemplate other = (CdsTemplate) obj;
      return JodaBeanUtils.equal(getConvention(), other.getConvention());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getConvention());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(64);
    buf.append("CdsTemplate{");
    buf.append("convention").append('=').append(JodaBeanUtils.toString(getConvention()));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code CdsTemplate}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code convention} property.
     */
    private final MetaProperty<CdsConvention> convention = DirectMetaProperty.ofImmutable(
        this, "convention", CdsTemplate.class, CdsConvention.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "convention");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 2039569265:  // convention
          return convention;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public CdsTemplate.Builder builder() {
      return new CdsTemplate.Builder();
    }

    @Override
    public Class<? extends CdsTemplate> beanType() {
      return CdsTemplate.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code convention} property.
     * @return the meta-property, not null
     */
    public MetaProperty<CdsConvention> convention() {
      return convention;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 2039569265:  // convention
          return ((CdsTemplate) bean).getConvention();
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
   * The bean-builder for {@code CdsTemplate}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<CdsTemplate> {

    private CdsConvention convention;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(CdsTemplate beanToCopy) {
      this.convention = beanToCopy.getConvention();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 2039569265:  // convention
          return convention;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 2039569265:  // convention
          this.convention = (CdsConvention) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public Builder set(MetaProperty<?> property, Object value) {
      super.set(property, value);
      return this;
    }

    @Override
    public Builder setString(String propertyName, String value) {
      setString(meta().metaProperty(propertyName), value);
      return this;
    }

    @Override
    public Builder setString(MetaProperty<?> property, String value) {
      super.setString(property, value);
      return this;
    }

    @Override
    public Builder setAll(Map<String, ? extends Object> propertyValueMap) {
      super.setAll(propertyValueMap);
      return this;
    }

    @Override
    public CdsTemplate build() {
      return new CdsTemplate(
          convention);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the {@code convention} property in the builder.
     * @param convention  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder convention(CdsConvention convention) {
      JodaBeanUtils.notNull(convention, "convention");
      this.convention = convention;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(64);
      buf.append("CdsTemplate.Builder{");
      buf.append("convention").append('=').append(JodaBeanUtils.toString(convention));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
