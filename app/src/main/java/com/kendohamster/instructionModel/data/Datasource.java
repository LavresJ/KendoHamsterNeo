package com.kendohamster.instructionModel.data;

import com.kendohamster.R;
import com.kendohamster.instructionModel.Instruction;

import java.util.ArrayList;

public class Datasource {
    public ArrayList<Instruction> loadMenUchiIns(){

        ArrayList<Instruction> list = new ArrayList<Instruction>();

        list.add(new Instruction(com.kendohamster.R.string.menUchiInstruction_1, R.drawable.men_uchi_step1));
        list.add(new Instruction(com.kendohamster.R.string.menUchiInstruction_2, R.drawable.men_uchi_step2));
        list.add(new Instruction(com.kendohamster.R.string.menUchiInstruction_3, R.drawable.men_uchi_step3));

        return list;
    }

    public ArrayList<Instruction> loadSuriAshiIns(){

        ArrayList<Instruction> list = new ArrayList<Instruction>();

        list.add(new Instruction(R.string.suriAshiInstruction_1, R.drawable.suri_ashi_step1));
        list.add(new Instruction(R.string.suriAshiInstruction_2, R.drawable.suri_ashi_step2));
        list.add(new Instruction(R.string.suriAshiInstruction_3, R.drawable.suri_ashi_step3));

        return list;
    }

    public ArrayList<Instruction> loadWakiKiamaeIns(){

        ArrayList<Instruction> list = new ArrayList<Instruction>();

        list.add(new Instruction(R.string.wakiKiamaeInstruction, R.drawable.waki_kiamae));

        return list;
    }

    public ArrayList<Instruction> loadDouUchiIns(){

        ArrayList<Instruction> list = new ArrayList<Instruction>();

        list.add(new Instruction(R.string.douUchiInstruction_1, com.kendohamster.R.drawable.hamster1));
        list.add(new Instruction(R.string.douUchiInstruction_2, com.kendohamster.R.drawable.hamster2));
        list.add(new Instruction(R.string.douUchiInstruction_3, com.kendohamster.R.drawable.hamster3));

        return list;
    }
}
