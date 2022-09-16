package com.kendohamster.instructionModel.data;

import static android.os.Build.VERSION_CODES.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kendohamster.R;
import com.kendohamster.instructionModel.Instruction;

import java.util.ArrayList;

public class Datasource {
    public ArrayList<Instruction> loadFrontSwingIns(){

        ArrayList<Instruction> list = new ArrayList<Instruction>();

        list.add(new Instruction(com.kendohamster.R.string.frontSwingInstruction_1, com.kendohamster.R.drawable.hamster1));
        list.add(new Instruction(com.kendohamster.R.string.frontSwingInstruction_2, com.kendohamster.R.drawable.hamster2));
        list.add(new Instruction(com.kendohamster.R.string.frontSwingInstruction_3, com.kendohamster.R.drawable.hamster3));

        return list;
    }

    public ArrayList<Instruction> loadFootIns(){

        ArrayList<Instruction> list = new ArrayList<Instruction>();

        list.add(new Instruction(com.kendohamster.R.string.frontSwingInstruction_1, com.kendohamster.R.drawable.hamster1));
        list.add(new Instruction(com.kendohamster.R.string.frontSwingInstruction_2, com.kendohamster.R.drawable.hamster2));
        list.add(new Instruction(com.kendohamster.R.string.frontSwingInstruction_3, com.kendohamster.R.drawable.hamster3));

        return list;
    }

    public ArrayList<Instruction> loadHoldSwordIns(){

        ArrayList<Instruction> list = new ArrayList<Instruction>();

        list.add(new Instruction(com.kendohamster.R.string.frontSwingInstruction_1, com.kendohamster.R.drawable.hamster1));
        list.add(new Instruction(com.kendohamster.R.string.frontSwingInstruction_2, com.kendohamster.R.drawable.hamster2));
        list.add(new Instruction(com.kendohamster.R.string.frontSwingInstruction_3, com.kendohamster.R.drawable.hamster3));

        return list;
    }
}
