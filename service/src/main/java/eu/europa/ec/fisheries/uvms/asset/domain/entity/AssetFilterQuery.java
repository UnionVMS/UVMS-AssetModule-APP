package eu.europa.ec.fisheries.uvms.asset.domain.entity;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import static eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterQuery.ASSETFILTER_QUERY_FIND_ALL;
import static eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterQuery.ASSETFILTER_QUERY_GETBYID;
import static eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterQuery.ASSETFILTER_QUERY_CLEAR;
import static eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterQuery.ASSETFILTER_RETRIEVE_QUERYS_FOR_FILTER;
import static eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterQuery.ASSETFILTER_QUERY_BY_GUID;

@Entity
@Table(name = "assetfilterquery") // get index?
@NamedQueries({
		@NamedQuery(name=ASSETFILTER_QUERY_FIND_ALL, query="SELECT a FROM AssetFilterQuery a"),
		@NamedQuery(name=ASSETFILTER_QUERY_GETBYID, query="SELECT a FROM AssetFilterQuery a where a.id=:id"),
		@NamedQuery(name=ASSETFILTER_QUERY_CLEAR, query="DELETE  FROM AssetFilterQuery a where a.assetFilter=:assetfilter"),
		@NamedQuery(name=ASSETFILTER_RETRIEVE_QUERYS_FOR_FILTER, query="SELECT a  FROM AssetFilterQuery a where a.assetFilter=:assetfilter"),
		@NamedQuery(name=ASSETFILTER_QUERY_BY_GUID, query="SELECT a FROM AssetFilterQuery a WHERE a.id=:guid"),
})
@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class AssetFilterQuery  implements Serializable{

	private static final long serialVersionUID = 5321716442894183305L;
	
	public static final String ASSETFILTER_QUERY_FIND_ALL ="assetfilterquery.findAll";
	public static final String ASSETFILTER_QUERY_GETBYID = "assetfilterquery.getbyid";
	public static final String ASSETFILTER_QUERY_CLEAR = "assetfilterquery.clear";
	public static final String ASSETFILTER_RETRIEVE_QUERYS_FOR_FILTER = "assetfilterquery.retrievequerysforfilter";
	public static final String ASSETFILTER_QUERY_BY_GUID = "assetfilterquery.findByGuid";

	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

	@Size(max = 255)
    @Column(name = "type")
    private String type;
    
    @Column(name = "inverse")
    private boolean inverse;
    
    @Column(name = "isnumber")
    private boolean isNumber;
    
    @JsonManagedReference
    @OneToMany(mappedBy="assetFilterQuery", cascade = CascadeType.ALL)
    @Fetch(FetchMode.SELECT)
    private Set<AssetFilterValue> values;
    
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "assetfilter", foreignKey = @ForeignKey(name = "assetfilterquery_assetfilter_fk"))
    private AssetFilter assetFilter;
    
    
    public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isInverse() {
		return inverse;
	}

	public void setInverse(boolean inverse) {
		this.inverse = inverse;
	}

	public boolean getIsNumber() {
		return isNumber;
	}

	public void setIsNumber(boolean isNumber) {
		this.isNumber = isNumber;
	}
	
	public Set<AssetFilterValue> getValues() {
		return values;
	}

	public void setValues(Set<AssetFilterValue> values) {
		this.values = values;
	}
	
	public AssetFilter getAssetFilter() {
		return assetFilter;
	}

	public void setAssetFilter(AssetFilter assetFilter) {
		this.assetFilter = assetFilter;
	}

}