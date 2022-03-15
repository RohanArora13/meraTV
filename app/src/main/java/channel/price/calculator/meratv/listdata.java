/*******************************************************************************
 * Copyright (C) Rohan Arora - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by Rohan Arora <rohanarora1313@gmail.com> on 5/1/19 10:24 PM
 *
 * Last modified 5/1/19 10:24 PM
 *
 ******************************************************************************/

package channel.price.calculator.meratv;

public class listdata {

    private String list_name;
    private String list_price;
    private String list_uidc;

    public listdata(String list_name,String list_price,String list_uidc){


        this.list_name = list_name;
        this.list_price = list_price;
        this.list_uidc = list_uidc;
    }

    public void setList_name(String list_name) {
        this.list_name = list_name;
    }

    public void setList_price(String list_price) {
        this.list_price = list_price;
    }

    public void setList_uidc(String list_uidc) {
        this.list_uidc = list_uidc;
    }

    public String getList_name() {
        return list_name;
    }

    public String getList_price() {
        return list_price;
    }

    public String getList_uidc() {
        return list_uidc;
    }
}
