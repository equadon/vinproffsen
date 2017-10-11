package com.gitlab.uu.vinproffsen.items.storage;

/**
 * Storage class. Holds beverage details.
 *
 * @author Niklas Persson
 * @version 2016-03-13
 */
public class BeverageDetails {
    public final Integer volume;
    public final Integer pricePerLiter;
    public final Integer deposit;
    public final String packaging;
    public final String seal;
    public final Double alcohol;

    public BeverageDetails(Integer volume, Integer pricePerLiter, Integer deposit, String packaging, String seal, Double alcohol) {
        this.volume = volume;
        this.pricePerLiter = pricePerLiter;
        this.deposit = deposit;
        this.packaging = packaging;
        this.seal = seal;
        this.alcohol = alcohol;
    }
}
