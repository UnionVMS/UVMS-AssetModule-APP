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
//import org.junit.Test;
//
//import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelValidationException;
//import org.junit.Assert;
//
//public class TestAssetDataSourceRequestValidator {
//
//    @Test
//    public void testValidateImo() {
//        try {
//            AssetDataSourceRequestValidator.validateIMO("2345678");
//        } catch (AssetModelValidationException e) {
//            Assert.fail("IMO 2345678 should validate.");
//        }
//
//        try {
//            AssetDataSourceRequestValidator.validateIMO("A123B12");
//            Assert.fail("IMO A123B12 should not validate.");
//        } catch (AssetModelValidationException e) {
//            // Ok
//        }
//    }
//
//    @Test
//    public void testValidateMmsi() {
//        try {
//            AssetDataSourceRequestValidator.validateMMSI("234567801");
//        } catch (AssetModelValidationException e) {
//            Assert.fail("MMSI 234567801 should validate.");
//        }
//
//        try {
//            AssetDataSourceRequestValidator.validateMMSI("A123B12");
//            Assert.fail("IMO A123B12 should not validate.");
//        } catch (AssetModelValidationException e) {
//            // Ok
//        }
//    }
//
//    @Test
//    public void testCfrPattern() {
//        testValid("ABC123456789");
//        testValid("aBc000111222");
//        testValid("ABCABCABCABC");
//        testValid("ABC234A1C2BC");
//        testValid(null);
//
//        testInvalid("");
//        testInvalid("ABC123");
//        testInvalid("123456789012");
//        testInvalid("14535348948594594");
//        testInvalid("123ABCABCABC");
//    }
//
//    private void testValid(String cfr) {
//        try {
//            AssetDataSourceRequestValidator.validateCFR(cfr);
//        } catch (AssetModelValidationException e) {
//            Assert.fail("The cfr " + cfr + " should validate.");
//        }
//    }
//
//    private void testInvalid(String cfr) {
//        try {
//            AssetDataSourceRequestValidator.validateCFR(cfr);
//            Assert.fail("The cfr " + cfr + " should not validate.");
//        } catch (AssetModelValidationException e) {
//            // Ok
//        }
//    }
//
//}