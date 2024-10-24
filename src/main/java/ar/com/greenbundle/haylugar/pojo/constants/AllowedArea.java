package ar.com.greenbundle.haylugar.pojo.constants;
public enum AllowedArea {
    BARRIO_JARDIN_CORDOBA(new double[][]{
            {-64.18670739306111, -31.446675707592185}, {-64.18670739306111, -31.458402121076865},
            {-64.16876706237093, -31.458402121076865}, {-64.16876706237093, -31.446675707592185},
            {-64.18670739306111, -31.446675707592185}
    });

    public final double[][] coordinates;

    AllowedArea(double[][] coordinates) {
        this.coordinates = coordinates;
    }
}
