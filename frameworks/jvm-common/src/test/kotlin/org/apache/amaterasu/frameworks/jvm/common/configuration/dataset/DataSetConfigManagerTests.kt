/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.amaterasu.frameworks.jvm.common.configuration.dataset


import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

object DataSetConfigManagerTests : Spek({

    val marker = DataSetConfigManagerTests::class.java.getResource("/maki.yml")!!.path

    given("a ConfigManager for configuration for datasets") {
        val repoPath = "${File(marker).parent}/test_repo"
        val cfg = DataSetConfigManager("test", repoPath)

        it("should retrieve all configurations for File datasets") {
            val fileConfigs = cfg.getFileConfigs()
            assertEquals(1, fileConfigs.size)
            assertEquals("users", fileConfigs[0].name)
            assertEquals("parquet", fileConfigs[0].format)
            assertEquals("s3://filestore", fileConfigs[0].uri)
            assertEquals("overwrite", fileConfigs[0].mode)
        }

        it("should retrieve all configurations for the Hive datasets") {
            val hiveConfs = cfg.getHiveConfigs()
            assertEquals(2, hiveConfs.count())
            val hiveConfig = cfg.getHiveConfigByName("transactions")!!
            assertEquals("parquet", hiveConfig.format)
            assertEquals("/user/somepath", hiveConfig.uri)
            assertEquals("transations_daily", hiveConfig.database)
            assertEquals("transx", hiveConfig.table)
        }

        it("should retrieve specific configuration from the Hive dataset") {
            val hiveConfig = cfg.getHiveConfigByName("second_transactions")!!
            assertEquals("avro", hiveConfig.format)
            assertEquals("/seconduser/somepath", hiveConfig.uri)
            assertEquals("transations_monthly", hiveConfig.database)
            assertEquals("avro_table", hiveConfig.table)
        }

        it("should retrieve all generic configurations") {
            val genConfs = cfg.getGenericConfigs()
            assertEquals(1, genConfs.count())
            val genConfig = cfg.getGenericConfigByName("mydataset")
            assertEquals("param1value", genConfig!!.params["param1key"])
        }


        it("should return a null Config for an absent name lookup") {
            val hiveConf = cfg.getHiveConfigByName("non-existent")
            assertNull(hiveConf)
        }
    }

    given("a ConfigManager file filter") {
        it("should match the files with the expected pattern") {
            assertTrue(DataSetConfigManager.DATASET_YAML_FILE_FILTER(File.createTempFile("datasets", ".yml")))
            assertTrue(DataSetConfigManager.DATASET_YAML_FILE_FILTER(File.createTempFile("otherdatasets", ".yml")))
            assertTrue(DataSetConfigManager.DATASET_YAML_FILE_FILTER(File.createTempFile("my-datasets", ".yaml")))
            assertTrue(DataSetConfigManager.DATASET_YAML_FILE_FILTER(File.createTempFile("my-DATASETS", ".YAML")))
        }

        it("should filter files with the unexpected pattern") {
            assertFalse(DataSetConfigManager.DATASET_YAML_FILE_FILTER(File.createTempFile("dataset", ".yml")))
            assertFalse(DataSetConfigManager.DATASET_YAML_FILE_FILTER(File.createTempFile("otherdatasets", ".yml1")))
            assertFalse(DataSetConfigManager.DATASET_YAML_FILE_FILTER(File.createTempFile("my-dataseets", ".yaml")))
        }
    }
})
