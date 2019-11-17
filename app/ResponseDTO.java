package null;

import java.util.List;
import java.io.Serializable;

public class ResponseDTO implements Serializable {
	private int page;
	private int perPage;
	private int total;
	private int totalPages;
	private List<DataDTO> data;

	public void setPage(int page){
		this.page = page;
	}

	public int getPage(){
		return page;
	}

	public void setPerPage(int perPage){
		this.perPage = perPage;
	}

	public int getPerPage(){
		return perPage;
	}

	public void setTotal(int total){
		this.total = total;
	}

	public int getTotal(){
		return total;
	}

	public void setTotalPages(int totalPages){
		this.totalPages = totalPages;
	}

	public int getTotalPages(){
		return totalPages;
	}

	public void setData(List<DataDTO> data){
		this.data = data;
	}

	public List<DataDTO> getData(){
		return data;
	}

	@Override
 	public String toString(){
		return 
			"ResponseDTO{" + 
			"page = '" + page + '\'' + 
			",per_page = '" + perPage + '\'' + 
			",total = '" + total + '\'' + 
			",total_pages = '" + totalPages + '\'' + 
			",data = '" + data + '\'' + 
			"}";
		}
}