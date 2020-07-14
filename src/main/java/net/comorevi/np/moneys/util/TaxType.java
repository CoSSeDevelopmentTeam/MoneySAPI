package net.comorevi.np.moneys.util;

public enum TaxType {
    PAYMENT(1.05),
    USER_SHOP(1.05),
    ADMIN_SHOP(1.10),
    INCOME_LOWEST(1.05),
    INCOME_LOW(1.10),
    INCOME_MEDIUM(1.15),
    INCOME_HIGH(1.20),
    INCOME_HIGHEST(1.25);

    private final double ratio;

    private TaxType(double ratio) {
        this.ratio = ratio;
    }

    public double getRatio() {
        return ratio;
    }

    @Override
    public String toString() {
        return "TaxRatio{" +
                "ratio=" + ratio +
                '}';
    }
}
