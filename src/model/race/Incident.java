package model.race;

import model.components.AeroKit;
import model.components.Component;
import model.components.Suspension;

public enum Incident {
    SPIN("Разворот", 7, false),
    AERO_DAMAGE("Повреждение аэродинамического пакета", 30, false),
    COLLISION("Столкновение", 0, true),
    ENGINE_ERROR("Отказ двигателя", 0, true),
    BREAK_ERROR("Отказ тормозов", 0, true);

    private String type;
    private int time;
    private boolean fatal;

    Incident(String type, int time, boolean fatal) {
        this.type = type;
        this.time = time;
        this.fatal = fatal;

    }

    public String getType() {
        return type;
    }

    public int getTime() {
        return time;
    }

    public boolean isFatal() {
        return fatal;
    }
}
