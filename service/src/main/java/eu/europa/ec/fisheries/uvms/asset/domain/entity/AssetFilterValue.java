package eu.europa.ec.fisheries.uvms.asset.domain.entity;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import static eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterValue.ASSETFILTER_VALUE_FIND_ALL;
import static eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterValue.ASSETFILTER_VALUE_GETBYID;
import static eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterValue.ASSETFILTER_VALUE_CLEAR;
import static eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterValue.ASSETFILTER_RETRIEVE_VALUES_FOR_FILTER;

@Entity
@Table(name = "assetfiltervalue", indexes = { @Index(columnList = "assetfilter", name="assetfiltervalue_assetfilter_FK_INX12")}) // get index?
@NamedQueries({
		@NamedQuery(name=ASSETFILTER_VALUE_FIND_ALL, query="SELECT a FROM AssetFilterValue a"),
		@NamedQuery(name=ASSETFILTER_VALUE_GETBYID, query="SELECT a FROM AssetFilterValue a where a.id=:id"),
		@NamedQuery(name=ASSETFILTER_VALUE_CLEAR, query="DELETE  FROM AssetFilterValue a where a.assetFilter=:assetFilter"),
		@NamedQuery(name=ASSETFILTER_RETRIEVE_VALUES_FOR_FILTER, query="SELECT a  FROM AssetFilterValue a where a.assetFilter=:assetFilter"),
})
@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class AssetFilterValue implements Serializable{

	public static final String ASSETFILTER_VALUE_FIND_ALL ="assetfiltervalue.findAll";
	public static final String ASSETFILTER_VALUE_GETBYID = "assetfiltervalue.getbyid";
	public static final String ASSETFILTER_VALUE_CLEAR = "assetfiltervalue.clear";
	public static final String ASSETFILTER_RETRIEVE_VALUES_FOR_FILTER = "assetfiltervalue.retrievevaluesforfilter";

	private static final long serialVersionUID = 8898101336225121988L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;
	
	@Size(max = 255)
    @Column(name = "operator")
    private String operator;

    @Size(max = 255)
    @Column(name = "value")
    private String value;
    
    @Size(max = 255)
    @Column(name = "updatedby")
    private String updatedBy;
    
    @Column(name = "updatetime")
    private OffsetDateTime updateTime;
    
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "assetfilter", foreignKey = @ForeignKey(name = "assetfiltervalue_assetfilter_fk"))
    private AssetFilter assetFilter;

    public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public OffsetDateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(OffsetDateTime updateTime) {
		this.updateTime = updateTime;
	}

	public AssetFilter getAssetFilter() {
		return assetFilter;
	}

	public void setAssetFilter(AssetFilter assetFilter) {
		this.assetFilter = assetFilter;
	}

}
