package to.uk.ekbkloz.gbhw27.proto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Andrey on 04.09.2016.
 */
public class RoomsList extends ProtoObject {
    private static final long serialVersionUID = 6584092555736757740L;
    private ArrayList<String> list;

    public RoomsList(String[] list) {
        this.list = new ArrayList<String>(Arrays.asList(list));
    }

    public RoomsList(Collection<String> list) {
        this.list = new ArrayList<String>(list);
    }

    public ArrayList<String> getList() {
        return list;
    }
}
