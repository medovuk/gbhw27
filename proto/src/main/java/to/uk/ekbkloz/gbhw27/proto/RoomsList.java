package to.uk.ekbkloz.gbhw27.proto;

import java.util.Collection;

/**
 * Created by Andrey on 04.09.2016.
 */
public class RoomsList extends ProtoObject {
    private static final long serialVersionUID = 6584092555736757740L;
    private String[] list;

    public RoomsList(String[] list) {
        this.list = list;
    }

    public RoomsList(Collection<String> list) {
        this.list = list.toArray(new String[list.size()]);
    }

    public String[] getList() {
        return list;
    }
}
