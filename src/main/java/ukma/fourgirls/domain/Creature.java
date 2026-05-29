package ukma.fourgirls.domain;

abstract class Creature {
    private final String name;

    protected Creature(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
