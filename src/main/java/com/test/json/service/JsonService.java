package com.test.json.service;

import com.test.json.model.APIResponse;
import com.test.json.model.DataModel;
import com.test.json.repository.JsonRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
public class JsonService {

    private JsonRepository jsonRepository;

    public JsonService(@Qualifier("JsonRepository") JsonRepository jsonRepository) {
        this.jsonRepository = jsonRepository;
    }

    public APIResponse<Void> test() {
        APIResponse<Void> response = new APIResponse<>();
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--remote-allow-origins=*");

            System.setProperty("webdriver.chrome.driver", "/Users/prior/Downloads/chromedriver_mac64/chromedriver");
            WebDriver driver;
            driver = new ChromeDriver(options);

            List<String> symbol = this.jsonRepository.findSymbol();
            List<DataModel> dataModels = new ArrayList<>();
            List<String> symbolError = new ArrayList<>();

            for (int i=0; i < symbol.size(); i++) {
                String url = "https://www.set.or.th/th/market/product/stock/quote/" + symbol.get(i).replaceAll("\\s", "") + "/historical-trading";
                String urlError = "ขออภัย ไม่พบข้อมูลที่คุณต้องการ";
                driver.get(url);
                driver.navigate().refresh();

                Document doc = Jsoup.parse(driver.getPageSource());

                if (!doc.select("head > title").text().equals(urlError)) {
                    List<String> datapage = getDataListString(doc);
                    List<String> dataAllPage = new ArrayList<>();
                    dataAllPage.addAll(datapage);

                    Elements btnNextPage1 = doc.select("div > ul > li > button");
                    String a =  btnNextPage1.text().trim();
                    a = a.substring(a.length() - 1);
                    int size = Integer.parseInt(a) - 1;

                    for (int j = 0; j < size; j++) {
                        WebElement btnNextPage = driver.findElement(By.cssSelector("[aria-label=\"Go to next page\"]"));

                        JavascriptExecutor js = (JavascriptExecutor) driver;
                        js.executeScript("arguments[0].click();", btnNextPage);

                        doc = Jsoup.parse(driver.getPageSource());
                        datapage = getDataListString(doc);
                        dataAllPage.addAll(datapage);
                    }
                    dataModels = getDataListModel(dataAllPage, symbol.get(i));
                    this.jsonRepository.insertSymbol(dataModels);
                } else {
                    log.info("URL ERROR");
                    log.info("" + symbol.get(i));
                    symbolError.add(symbol.get(i));
                }
            }
            response.setCode("200");
            response.setMessage("Success");
        } catch (Exception e) {
            e.printStackTrace();
            response.setCode("500");
            response.setMessage(e.getMessage());
        }
        return response;
    }

    private List<String> getDataListString(Document doc){
        Elements result = doc.select("tbody > tr > td");
        List<String> data = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            if (!result.get(i).text().equals("")) {
                data.add(result.get(i).text());
            }
        }
        return data;
    }

    private List<DataModel> getDataListModel(List<String> result, String symbol) throws ParseException {
        int index = 0;
        List<DataModel> dataModelList = new ArrayList<>();
        for (int i=0; i < result.size()/9; i++) {
            DataModel dataModel = new DataModel();

            dataModel.setSymbol(symbol);

            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy", new Locale("th", "TH"));
            java.util.Date date = formatter.parse(result.get(index));
            LocalDate localDate = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate transactionDate = LocalDate.parse(localDate.format(outputFormatter), outputFormatter);
            dataModel.setTransactionDate(transactionDate);
            index++;

            if (result.get(index).equals("-")){
                dataModel.setOpenPrice(null);
                index++;
            } else {
                double openPrice = Double.parseDouble(result.get(index).replace(",", ""));
                dataModel.setOpenPrice(openPrice);
                index++;
            }

            if (result.get(index).equals("-")){
                dataModel.setMaxPrice(null);
                index++;
            } else {
                double maxPrice = Double.parseDouble(result.get(index).replace(",", ""));
                dataModel.setMaxPrice(maxPrice);
                index++;
            }

            if (result.get(index).equals("-")){
                dataModel.setMinPrice(null);
                index++;
            } else {
                double minPrice = Double.parseDouble(result.get(index).replace(",", ""));
                dataModel.setMinPrice(minPrice);
                index++;
            }

            if (result.get(index).equals("-")){
                dataModel.setClosePrice(null);
                index++;
            } else {
                double closePrice = Double.parseDouble(result.get(index).replace(",", ""));
                dataModel.setClosePrice(closePrice);
                index++;
            }

            if (result.get(index).equals("-")){
                dataModel.setChangePrice(null);
                index++;
            } else {
                double changePrice = Double.parseDouble(result.get(index).replace(",", ""));
                dataModel.setChangePrice(changePrice);
                index++;
            }

            if (result.get(index).equals("-")){
                dataModel.setChangeRatio(null);
                index++;
            } else {
                double changeRatio = Double.parseDouble(result.get(index).replace(",", ""));
                dataModel.setChangeRatio(changeRatio);
                index++;
            }

            if (result.get(index).equals("-")){
                dataModel.setVolume(null);
                index++;
            } else {
                double volume = Double.parseDouble(result.get(index).replace(",", ""));
                dataModel.setVolume(Double.valueOf(volume));
                index++;
            }

            if (result.get(index).equals("-")){
                dataModel.setReason(null);
                index++;
            } else {
                dataModel.setReason(result.get(index));
                index++;
            }

            dataModelList.add(dataModel);
        }
        return dataModelList;
    }

}
