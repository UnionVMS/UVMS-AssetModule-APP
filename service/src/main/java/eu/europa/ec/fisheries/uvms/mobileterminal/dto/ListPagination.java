package eu.europa.ec.fisheries.uvms.mobileterminal.dto;

public class ListPagination {

    private int page;
    private int listSize;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getListSize() {
        return listSize;
    }

    public void setListSize(int listSize) {
        this.listSize = listSize;
    }
}
