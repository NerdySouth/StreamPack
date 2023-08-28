/*
 * Copyright (C) 2023 Thibault B.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.thibaultbee.streampack.internal.muxers.mp4.boxes

import io.github.thibaultbee.streampack.internal.utils.extensions.toByteArray
import io.github.thibaultbee.streampack.utils.ResourcesUtils
import org.junit.Assert.assertArrayEquals
import org.junit.Test

class SampleSizeBoxTest {
    @Test
    fun `write valid stsz test`() {
        val expectedBuffer = ResourcesUtils.readMP4ByteBuffer("stsz.box")
        val stsz = SampleSizeBox(
            sampleSizeEntries = listOf(
                23738,
                998,
                1556,
                3325,
                1158,
                1994,
                1676,
                1215,
                2095,
                1421,
                1763,
                2475,
                1448,
                1409,
                1233,
                1380,
                1666,
                1240,
                2096,
                1429,
                2161,
                1040,
                1440,
                1405,
                1270,
                1367,
                1682,
                1691,
                2228,
                1505,
                28381,
                889,
                1383,
                1276,
                1167,
                1367,
                1570,
                1206,
                2088,
                1466,
                1741,
                1069,
                1451,
                1393,
                1196,
                1363,
                1667,
                1247,
                2091,
                1475,
                2164,
                1043,
                1324,
                1402,
                1206,
                1366,
                1671,
                1638,
                2223,
                1548,
                29074,
                881,
                1382,
                1274,
                1167,
                1366,
                1568,
                1201,
                2088,
                1457,
                1736,
                1077,
                1453,
                1393,
                1196,
                1366,
                1668,
                1246,
                2091,
                1477,
                2161,
                1040,
                1331,
                1399,
                1208,
                1364,
                1674,
                1643,
                2224,
                1511,
                29400,
                881,
                1382,
                1274,
                1161,
                1359,
                1575,
                1198,
                2084,
                1465,
                1738,
                1068,
                1458,
                1402,
                1190,
                1369,
                1661,
                1238,
                2090,
                1477,
                2149,
                1036,
                1448,
                1387,
                1243,
                1372,
                1665,
                1629,
                2204,
                1511,
                28662,
                881,
                1385,
                1274,
                1168,
                1361,
                1568,
                1198,
                2090,
                1462,
                1747,
                1055,
                1456,
                1395,
                1192,
                1363,
                1668,
                1228,
                2089,
                1475,
                2143,
                1031,
                1454,
                1395,
                1205,
                1375,
                1663,
                1622,
                2261,
                1508,
                29115,
                889,
                1380,
                1274,
                1168,
                1365,
                1569,
                1203,
                2089,
                1464,
                1740,
                1069,
                1450,
                1391,
                1196,
                1362,
                1668,
                1251,
                2093,
                1473,
                2165,
                1041,
                1326,
                1400,
                1206,
                1364,
                1671,
                1640,
                2229,
                1547,
                29463,
                889,
                1383,
                1276,
                1168,
                1365,
                1569,
                1202,
                2093,
                1458,
                1737,
                1068,
                1450,
                1390,
                1191,
                1370,
                1665,
                1245,
                2057,
                1471,
                2166,
                1051,
                1451,
                1394,
                1199,
                1369,
                1671,
                1643,
                2220,
                1517,
                28757,
                887,
                1384,
                1276,
                1165,
                1371,
                1562,
                1206,
                2086,
                1462,
                1744,
                1073,
                1456,
                1392,
                1202,
                1367,
                1662,
                1245,
                2086,
                1451,
                2160,
                1045,
                1334,
                1403,
                1242,
                1367,
                1674,
                1640,
                2222,
                1516,
                29728,
                880,
                1384,
                1276,
                1168,
                1367,
                1569,
                1203,
                2090,
                1458,
                1742,
                1074,
                1452,
                1395,
                1196,
                1364,
                1665,
                1248,
                2089,
                1475,
                2161,
                1036,
                1329,
                1400,
                1208,
                1362,
                1674,
                1644,
                2227,
                1508,
                29463,
                887,
                1382,
                1274,
                1165,
                1365,
                1562,
                1201,
                2083,
                1461,
                1737,
                1075,
                1457,
                1392,
                1203,
                1369,
                1662,
                1245,
                2085,
                1452,
                2159,
                1046,
                1335,
                1402,
                1243,
                1367,
                1673,
                1638,
                2221,
                1516,
                28528,
                880,
                1384,
                1278,
                1162,
                1358,
                1576,
                1203,
                2087,
                1462,
                1742,
                1068,
                1456,
                1405,
                1191,
                1363,
                1657,
                1240,
                2089,
                1475,
                2148,
                1027,
                1447,
                1384,
                1237,
                1360,
                1665,
                1633,
                2213,
                1512,
                27525,
                886,
                1384,
                1277,
                1170,
                1364,
                1658,
                1206,
                2083,
                1452,
                1746,
                1092,
                1456,
                1401,
                1203,
                1364,
                1664,
                1247,
                2058,
                1476,
                2155,
                1042,
                1456,
                1401,
                1237,
                1373,
                1675,
                1637,
                2216,
                1525,
                28214,
                886,
                1382,
                1275,
                1168,
                1366,
                1569,
                1202,
                2092,
                1453,
                1742,
                1057,
                1453,
                1398,
                1195,
                1372,
                1663,
                1245,
                2095,
                1453,
                2163,
                1041,
                1331,
                1402,
                1204,
                1362,
                1673,
                1646,
                2221,
                1512,
                28531,
                881,
                1383,
                1276,
                1161,
                1360,
                1581,
                1199,
                2087,
                1460,
                1740,
                1064,
                1457,
                1404,
                1191,
                1366,
                1665,
                1240,
                2093,
                1475,
                2157,
                1037,
                1441,
                1396,
                1243,
                1375,
                1673,
                1632,
                2222,
                1512,
                27795,
                888,
                1383,
                1274,
                1165,
                1360,
                1572,
                1199,
                2086,
                1469,
                1735,
                1067,
                1459,
                1393,
                1205,
                1368,
                1666,
                1241,
                2063,
                1479,
                2149,
                1030,
                1447,
                1403,
                1201,
                1375,
                1676,
                1616,
                2253,
                1558,
                28238,
                880,
                1384,
                1274,
                1167,
                1363,
                1568,
                1200,
                2091,
                1458,
                1738,
                1071,
                1452,
                1394,
                1194,
                1363,
                1666,
                1244,
                2094,
                1474,
                2164,
                1038,
                1332,
                1403,
                1209,
                1360,
                1673,
                1644,
                2227,
                1511,
                28590,
                881,
                1384,
                1276,
                1168,
                1366,
                1570,
                1202,
                2092,
                1459,
                1738,
                1077,
                1452,
                1394,
                1193,
                1362,
                1668,
                1246,
                2093,
                1475,
                2161,
                1041,
                1329,
                1401,
                1206,
                1362,
                1674,
                1636,
                2230,
                1513,
                27894,
                886,
                1382,
                1277,
                1163,
                1370,
                1571,
                1205,
                2091,
                1455,
                1740,
                1073,
                1455,
                1400,
                1194,
                1373,
                1664,
                1248,
                2098,
                1452,
                2161,
                1044,
                1331,
                1400,
                1208,
                1362,
                1676,
                1643,
                2223,
                1518,
                28874,
                884,
                1382,
                1274,
                1168,
                1362,
                1568,
                1202,
                2092,
                1460,
                1740,
                1069,
                1458,
                1398,
                1193,
                1370,
                1664,
                1243,
                2096,
                1472,
                2161,
                1035,
                1327,
                1396,
                1238,
                1357,
                1672,
                1644,
                2225,
                1517,
                28602,
                886,
                1382,
                1275,
                1168,
                1366,
                1569,
                1202,
                2092,
                1453,
                1742,
                1060,
                1453,
                1398,
                1195,
                1372,
                1663,
                1245,
                2095,
                1453,
                2163,
                1041,
                1331,
                1402,
                1204,
                1362,
                1673,
                1641,
                2220,
                1512,
                29219,
                883,
                1381,
                1277,
                1196,
                1357,
                1573,
                1199,
                2083,
                1454,
                1741,
                1065,
                1452,
                1399,
                1225,
                1365,
                1654,
                1234,
                2086,
                1456,
                2145,
                1032,
                1443,
                1388,
                1199,
                1369,
                1669,
                1642,
                2218,
                1517,
                28209,
                887,
                1382,
                1274,
                1166,
                1365,
                1562,
                1199,
                2083,
                1462,
                1741,
                1067,
                1456,
                1390,
                1200,
                1366,
                1662,
                1243,
                2085,
                1451,
                2161,
                1046,
                1333,
                1403,
                1245,
                1363,
                1674,
                1653,
                2221,
                1520,
                28934,
                883,
                1384,
                1277,
                1167,
                1361,
                1568,
                1205,
                2091,
                1463,
                1745,
                1074,
                1457,
                1399,
                1192,
                1365,
                1662,
                1244,
                2097,
                1470,
                2160,
                1031,
                1327,
                1398,
                1239,
                1357,
                1670,
                1657,
                2222,
                1518,
                29231,
                889,
                1381,
                1273,
                1161,
                1358,
                1578,
                1196,
                2081,
                1467,
                1740,
                1064,
                1450,
                1399,
                1186,
                1368,
                1665,
                1236,
                2047,
                1480,
                2148,
                1036,
                1456,
                1394,
                1200,
                1366,
                1666,
                1640,
                2222,
                1549,
                28481,
                888,
                1381,
                1270,
                1169,
                1362,
                1571,
                1200,
                2089,
                1462,
                1739,
                1064,
                1345,
                1397,
                1193,
                1375,
                1664,
                1232,
                2055,
                1477,
                2162,
                1030,
                1452,
                1400,
                1240,
                1362,
                1676,
                1647,
                2221,
                1545,
                28937,
                885,
                1382,
                1278,
                1169,
                1360,
                1569,
                1198,
                2087,
                1461,
                1735,
                1063,
                1449,
                1392,
                1193,
                1361,
                1665,
                1244,
                2094,
                1475,
                2164,
                1040,
                1325,
                1403,
                1204,
                1362,
                1669,
                1653,
                2227,
                1551,
                29287,
                881,
                1381,
                1275,
                1167,
                1364,
                1568,
                1198,
                2088,
                1454,
                1734,
                1063,
                1459,
                1396,
                1193,
                1370,
                1663,
                1242,
                2096,
                1473,
                2164,
                1039,
                1324,
                1402,
                1244,
                1363,
                1672,
                1654,
                2220,
                1505,
                28478,
                943,
                1303,
                1275,
                1054,
                1364,
                1649,
                1181,
                2006,
                1440,
                1649,
                1088,
                1265,
                1414,
                1062,
                1379,
                1651,
                1176,
                2008,
                1443,
                2170,
                1114,
                1265,
                1331,
                1078,
                1345,
                1658,
                1457,
                2238,
                1473,
                29561,
                878,
                1382,
                1277,
                1169,
                1362,
                1571,
                1200,
                2091,
                1459,
                1736,
                1067,
                1455,
                1394,
                1194,
                1362,
                1666,
                1244,
                2091,
                1474,
                2163,
                1037,
                1325,
                1403,
                1208,
                1362,
                1667,
                1659,
                2233,
                1511,
                29311,
                884,
                1382,
                1274,
                1167,
                1363,
                1570,
                1201,
                2089,
                1461,
                1741,
                1070,
                1459,
                1397,
                1193,
                1367,
                1666,
                1243,
                2095,
                1472,
                2161,
                1031,
                1328,
                1398,
                1238,
                1356,
                1672,
                1649,
                2221,
                1513,
                29357,
                881,
                1384,
                1275,
                1160,
                1359,
                1579,
                1204,
                2088,
                1462,
                1739,
                1078,
                1451,
                1403,
                1191,
                1365,
                1668,
                1241,
                2090,
                1485,
                2148,
                1030,
                1449,
                1387,
                1205,
                1366,
                1671,
                1628,
                2209,
                1513,
                28364,
                888,
                1383,
                1274,
                1168,
                1364,
                1569,
                1201,
                2090,
                1465,
                1736,
                1071,
                1450,
                1392,
                1193,
                1362,
                1667,
                1245,
                2093,
                1476,
                2163,
                1041,
                1325,
                1404,
                1202,
                1363,
                1672,
                1635,
                2223,
                1549,
                29057,
                880,
                1384,
                1274,
                1168,
                1364,
                1568,
                1199,
                2092,
                1458,
                1738,
                1076,
                1451,
                1393,
                1193,
                1363,
                1667,
                1245,
                2092,
                1476,
                2160,
                1041,
                1329,
                1403,
                1208,
                1362,
                1674,
                1643,
                2223,
                1514,
                29387,
                881,
                1384,
                1275,
                1161,
                1360,
                1576,
                1199,
                2088,
                1470,
                1739,
                1073,
                1455,
                1404,
                1188,
                1365,
                1661,
                1239,
                2092,
                1475,
                2148,
                1037,
                1446,
                1389,
                1240,
                1369,
                1664,
                1627,
                2209,
                1513,
                28641,
                880,
                1384,
                1271,
                1169,
                1360,
                1569,
                1196,
                2091,
                1462,
                1736,
                1063,
                1460,
                1391,
                1205,
                1370,
                1666,
                1234,
                2061,
                1480,
                2149,
                1026,
                1453,
                1406,
                1193,
                1369,
                1665,
                1629,
                2265,
                1512,
                29091,
                886,
                1382,
                1278,
                1170,
                1364,
                1569,
                1199,
                2086,
                1463,
                1738,
                1076,
                1453,
                1391,
                1195,
                1362,
                1666,
                1245,
                2092,
                1475,
                2163,
                1039,
                1327,
                1401,
                1206,
                1361,
                1669,
                1643,
                2225,
                1546,
                29434,
                889,
                1381,
                1275,
                1168,
                1363,
                1568,
                1199,
                2090,
                1456,
                1737,
                1071,
                1451,
                1388,
                1193,
                1371,
                1666,
                1241,
                2054,
                1471,
                2165,
                1047,
                1450,
                1393,
                1197,
                1368,
                1671,
                1641,
                2217,
                1518,
                28734,
                886,
                1384,
                1274,
                1166,
                1368,
                1562,
                1201,
                2088,
                1461,
                1739,
                1072,
                1455,
                1392,
                1200,
                1366,
                1661,
                1243,
                2087,
                1451,
                2159,
                1045,
                1334,
                1405,
                1239,
                1364,
                1673,
                1637,
                2222,
                1517,
                29707,
                881,
                1384,
                1276,
                1168,
                1366,
                1568,
                1202,
                2092,
                1458,
                1737,
                1081,
                1451,
                1395,
                1194,
                1361,
                1667,
                1246,
                2097,
                1477,
                2158,
                1036,
                1330,
                1401,
                1203,
                1364,
                1673,
                1639,
                2225,
                1514,
                29449,
                886,
                1384,
                1274,
                1166,
                1363,
                1562,
                1199,
                2087,
                1461,
                1739,
                1074,
                1454,
                1391,
                1200,
                1366,
                1660,
                1244,
                2086,
                1451,
                2158,
                1048,
                1334,
                1406,
                1243,
                1365,
                1674,
                1639,
                2219,
                1520,
                28941,
                883,
                1382,
                1282,
                1162,
                1361,
                1577,
                1202,
                2087,
                1454,
                1737,
                1063,
                1453,
                1404,
                1191,
                1366,
                1658,
                1241,
                2088,
                1454,
                2147,
                1036,
                1441,
                1395,
                1199,
                1371,
                1668,
                1629,
                2223,
                1546,
                27947,
                880,
                1384,
                1279,
                1163,
                1364,
                1570,
                1204,
                2092,
                1463,
                1740,
                1086,
                1456,
                1393,
                1196,
                1362,
                1667,
                1248,
                2097,
                1472,
                2162,
                1035,
                1326,
                1403,
                1210,
                1362,
                1667,
                1645,
                2232,
                1520,
                28629,
                886,
                1382,
                1278,
                1168,
                1366,
                1569,
                1202,
                2092,
                1453,
                1742,
                1060,
                1453,
                1398,
                1195,
                1372,
                1663,
                1245,
                2095,
                1453,
                2163,
                1041,
                1331,
                1402,
                1204,
                1362,
                1673,
                1645,
                2220,
                1512,
                28946,
                881,
                1384,
                1278,
                1161,
                1362,
                1582,
                1200,
                2087,
                1463,
                1741,
                1075,
                1449,
                1402,
                1191,
                1366,
                1668,
                1240,
                2089,
                1481,
                2149,
                1034,
                1447,
                1384,
                1207,
                1365,
                1671,
                1626,
                2208,
                1508,
                28214,
                887,
                1383,
                1276,
                1165,
                1360,
                1572,
                1198,
                2089,
                1463,
                1732,
                1084,
                1459,
                1390,
                1209,
                1374,
                1669,
                1240,
                2090,
                1480,
                2145,
                1027,
                1448,
                1402,
                1236,
                1374,
                1674,
                1619,
                2215,
                1558
            )
        )
        val buffer = stsz.toByteBuffer()
        assertArrayEquals(expectedBuffer.toByteArray(), buffer.toByteArray())
    }
}