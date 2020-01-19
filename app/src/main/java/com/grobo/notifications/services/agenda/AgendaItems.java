package com.grobo.notifications.services.agenda;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Keep
public class AgendaItems {

    @SerializedName("agendas")
    @Expose
    private List<Agenda> agendas = null;

    public List<Agenda> getAgendas() {
        return agendas;
    }

    public void setAgendas(List<Agenda> agendas) {
        this.agendas = agendas;
    }
}

