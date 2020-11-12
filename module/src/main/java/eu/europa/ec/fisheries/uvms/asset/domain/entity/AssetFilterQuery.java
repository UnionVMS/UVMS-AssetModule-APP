package eu.europa.ec.fisheries.uvms.asset.domain.entity;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

import eu.europa.ec.fisheries.uvms.asset.domain.constant.AssetFilterValueType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.Size;

import static eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterQuery.ASSETFILTER_QUERY_FIND_ALL;
import static eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterQuery.ASSETFILTER_QUERY_GETBYID;
import static eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterQuery.ASSETFILTER_QUERY_CLEAR;
import static eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterQuery.ASSETFILTER_RETRIEVE_QUERYS_FOR_FILTER;

@Entity
@Table(name = "assetfilterquery")
@NamedQueries({
		@NamedQuery(name=ASSETFILTER_QUERY_FIND_ALL, query="SELECT a FROM AssetFilterQuery a"),
		@NamedQuery(name=ASSETFILTER_QUERY_GETBYID, query="SELECT a FROM AssetFilterQuery a where a.id =:id"),
		@NamedQuery(name=ASSETFILTER_QUERY_CLEAR, query="DELETE  FROM AssetFilterQuery a where a.assetFilter = :assetFilter"),
		@NamedQuery(name=ASSETFILTER_RETRIEVE_QUERYS_FOR_FILTER, query="SELECT a FROM AssetFilterQuery a where a.assetFilter = :assetFilter")
})
public class AssetFilterQuery  implements Serializable{

	private static final long serialVersionUID = 5321716442894183305L;
	
	public static final String ASSETFILTER_QUERY_FIND_ALL ="assetfilterquery.findAll";
	public static final String ASSETFILTER_QUERY_GETBYID = "assetfilterquery.getbyid";
	public static final String ASSETFILTER_QUERY_CLEAR = "assetfilterquery.clear";
	public static final String ASSETFILTER_RETRIEVE_QUERYS_FOR_FILTER = "assetfilterquery.retrievequerysforfilter";
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

	@Size(max = 255)
    @Column(name = "type")
    private String type;
    
    @Column(name = "inverse")
    private boolean inverse;

	@Enumerated(EnumType.STRING)
    @Column(name = "value_type")
    private AssetFilterValueType valueType;
    
    @OneToMany(mappedBy="assetFilterQuery", cascade = CascadeType.ALL)
    @Fetch(FetchMode.SELECT)
    private Set<AssetFilterValue> values;
    
    @JsonbTransient
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

	public boolean getInverse() {
		return inverse;
	}

	public void setInverse(boolean inverse) {
		this.inverse = inverse;
	}

	public AssetFilterValueType getValueType() {
		return valueType;
	}

	public void setValueType(AssetFilterValueType valueType) {
		this.valueType = valueType;
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
