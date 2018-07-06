///*
//﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
//© European Union, 2015-2016.
//
//This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
//redistribute it and/or modify it under the terms of the GNU General Public License as published by the
//Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
//the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
//copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
// */
//package eu.europa.ec.fisheries.uvms.asset.model.mapper;
//
//import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelValidationException;
//import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
//import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
//
//public class AssetDataSourceRequestValidator {
//
//    private static final String CFR_PATTERN = "^[a-zA-ZåäöÅÄÖ]{3}[a-zA-ZåäöÅÄÖ0-9]{9}$";
//    private static final int MAX_IRCS_LENGTH = 8;
//    private static final int COUNTRY_CODE_LENGTH = 3;
//    private static final int MAX_NAME = 40;
//    private static final int MAX_HOMEPORT = 30;
//    private static final int MAX_EXTERNAL_MARKING = 14;
//	private static final String IMO_PATTERN = "^\\d{7}$";
//	private static final String MMSI_PATTERN = "^\\d{9}$";
//	private static final String[] YES_NO_UNKNOWN = new String[]{"Y","N","U"};
//
//    public static void validateCreateAsset(Asset asset) throws AssetModelValidationException {
//        validateAsset(asset);
//    }
//
//    public static void validateUpdateAsset(Asset asset) throws AssetModelValidationException {
//        validateAssetId(asset.getAssetId());
//        validateAsset(asset);
//    }
//
//    private static void validateAssetId(AssetId assetId) throws AssetModelValidationException {
//        if (assetId.getType() == null) {
//            throw new AssetModelValidationException("No id type");
//        }
//        if (assetId.getValue() == null || assetId.getValue().isEmpty()) {
//            throw new AssetModelValidationException("No id value");
//        }
//    }
//
//    private static void validateAsset(Asset asset) throws AssetModelValidationException {
//        validateCFR(asset.getCfr());
//        validateIRCS(asset.getIrcs());
//        validateIrcsIndicator(asset.getHasIrcs());
//        validateCountry(asset.getCountryCode());
//        validateName(asset.getName());
//        validateHomePort(asset.getHomePort());
//        validateExternalMarking(asset.getExternalMarking());
//        if(asset.getImo() != null) {
//        	validateIMO(asset.getImo());
//        }
//        if( asset.getMmsiNo() != null) {
//        	validateMMSI(asset.getMmsiNo());
//        }
//    }
//
//    public static void validateName(String name) throws AssetModelValidationException {
//    	if(name != null) {
//    		if(name.length() > MAX_NAME) {
//    			throw new AssetModelValidationException("Asset Name is too long");
//    		}
//    	}
//    }
//    public static void validateHomePort(String homeport) throws AssetModelValidationException {
//    	if(homeport != null) {
//    		if (homeport.length() > MAX_HOMEPORT) {
//    			throw new AssetModelValidationException("Homeport is too long");
//    		}
//    	}
//    }
//
//    public static void validateCFR(String cfr) throws AssetModelValidationException {
//    	if(cfr != null) {
//    		if(!cfr.matches(CFR_PATTERN)) {
//    			throw new AssetModelValidationException("CFR is too long");
//    		}
//    	}
//    }
//
//    public static void validateIRCS(String ircs) throws AssetModelValidationException {
//    	if(ircs != null) {
//    		if(ircs.length() > MAX_IRCS_LENGTH) {
//    			throw new AssetModelValidationException("IRCS is too long. Max " + MAX_IRCS_LENGTH);
//    		}
//    	}
//    }
//
//    public static void validateIrcsIndicator(String ircsIndicator) throws AssetModelValidationException {
//        if (ircsIndicator != null) {
//        	boolean validIrcsIndicator = false;
//        	for(String validValue : YES_NO_UNKNOWN) {
//        		if(validValue.equalsIgnoreCase(ircsIndicator)) {
//        			validIrcsIndicator = true;
//        		}
//        	}
//        	if(!validIrcsIndicator) {
//        		throw new AssetModelValidationException("IRCS indicator invalid. Valid values Y, N and U");
//        	}
//        }
//    }
//
//    public static void validateIMO(String imo) throws AssetModelValidationException {
//    	if(imo != null) {
//    	    if (!imo.matches(IMO_PATTERN)) {
//            	throw new AssetModelValidationException("IMO number is invalid. Must be 7 digits.");
//            }
//    	}
//    }
//
//	public static void validateMMSI(String value) throws AssetModelValidationException {
//		if(value != null) {
//			if(!value.matches(MMSI_PATTERN)) {
//				throw new AssetModelValidationException("MMSI number is invalid. Must be 9 digits");
//			}
//		}
//	}
//
//	public static void validateBoolean(String value) throws AssetModelValidationException {
//		if(value != null) {
//			if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
//			} else {
//				throw new AssetModelValidationException("Boolean value should be 'true' or 'false'");
//			}
//		}
//	}
//
//	public static void validateCountry(String countryCode) throws AssetModelValidationException {
//		if(countryCode != null) {
//			if (countryCode.length() != COUNTRY_CODE_LENGTH) {
//				throw new AssetModelValidationException("Country code / flag state is invalid");
//			}
//		}
//	}
//
//	public static void validateExternalMarking(String value) throws AssetModelValidationException {
//		if (value != null) {
//			if(value.length() > MAX_EXTERNAL_MARKING) {
//				throw new AssetModelValidationException("External marking is too long");
//			}
//        }
//	}
//}