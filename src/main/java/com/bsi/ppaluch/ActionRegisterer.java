package com.bsi.ppaluch;


import com.bsi.ppaluch.dao.*;
import com.bsi.ppaluch.entity.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;


import static com.bsi.ppaluch.CurrentLoggedUser.getUser;

public class ActionRegisterer {

    @Autowired
    FunctionRepository functionRepository;
    @Autowired
    private FunctionRunRepository functionRunRepository;
    @Autowired
    private TableNameRepository tableNameRepository;
    @Autowired
    private DataChangeRepository dataChangeRepository;
    @Autowired
    private ActionTypeRepository actionTypeRepository;
    @Autowired
    private PasswordRepository passwordRepository;

    public void registerAction(String name) {
        Function function = functionRepository.findByName(name);
        FunctionRun functionRun = createFunctionRun(function);
        functionRunRepository.save(functionRun);
    }

    private FunctionRun createFunctionRun(Function function) {
        FunctionRun functionRun = new FunctionRun();
        functionRun.setDateTime(new Date());
        functionRun.setFunction(function);
        functionRun.setUser(getUser());
        return functionRun;
    }

    public void registerPasswordDataChange(Password previousPass, Password newPass, String action, int id) {
        DataChange dataChange = createDataChange(id,action);
        switch(action) {
            case "add":
                dataChange.setPreviousRecordValue(null);
                dataChange.setPresentRecordValue(getRecordValueString(newPass));
                break;
            case "save_edited":
            case "recover":
                dataChange.setPreviousRecordValue(getRecordValueString(previousPass));
                dataChange.setPresentRecordValue(getRecordValueString(newPass));
                break;
            case "delete":
                dataChange.setPreviousRecordValue(getRecordValueString(previousPass));
                dataChange.setPresentRecordValue(null);
                break;
            default:
                break;
        }
        dataChangeRepository.save(dataChange);
    }

    private DataChange createDataChange(int id, String action) {
        DataChange dataChange = new DataChange();
        dataChange.setDateTime(new Date());
        dataChange.setIdModifiedRecord(id);
        dataChange.setTableName(tableNameRepository.findById(1).get());
        dataChange.setUser(getUser());
        dataChange.setActionType(actionTypeRepository.findByTitle(action));
        return dataChange;
    }

    private String getRecordValueString(Password pass) {
        return pass.getDescription() + "," + pass.getLogin() + "," + pass.getPassword() + "," + pass.getWeb_address();
    }

    public void recoverDataChange(Integer id) {
        DataChange change = dataChangeRepository.findById(id).get();
        if("passwords".equals(change.getTableName().getName())) {
            Password password = passwordRepository.findByPasswordId(change.getIdModifiedRecord());
            Password newPass;
            switch (change.getActionType().getTitle()) {
                case "add":
                    newPass = password;
                    newPass.setDeleted(true);
                    break;
                case "save_edited":
                case "recover":
                case "delete":
                    newPass = createNewPass(password, change.getPreviousRecordValue());
                    newPass.setDeleted(false);
                    break;
                default:
                    newPass = password;
            }
            passwordRepository.save(newPass);
            registerPasswordDataChange(password, newPass, "recover", password.getId());
        }
    }

    private Password createNewPass(Password password, String version) {
        String[] arrOfStr = version.split(",");
        password.setId(password.getId());
        password.setDescription(arrOfStr[0]);
        password.setLogin(arrOfStr[1]);
        password.setPassword(arrOfStr[2]);
        password.setWeb_address(arrOfStr[3]);
        return password;
    }
}
