package com.progressoft.tools;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoringSummaryImpl  implements ScoringSummary {

    public ScoringSummaryImpl(List<BigDecimal> col) {
        this.col = col;
    }
    private BigDecimal average;
    private BigDecimal sumFromStandardDeviation;
    private List<BigDecimal>col=new ArrayList<BigDecimal>();

    public BigDecimal getAverage() {
        return average;
    }

    public void setAverage(BigDecimal average) {
        this.average = average;
    }

    public BigDecimal getSumFromStandardDeviation() {
        return sumFromStandardDeviation;
    }

    public void setSumFromStandardDeviation(BigDecimal sumFromStandardDeviation) {
        this.sumFromStandardDeviation = sumFromStandardDeviation;
    }

    public List<BigDecimal> getCol() {
        return col;
    }

    public void setCol(List<BigDecimal> col) {
        this.col = col;
    }

    @Override
    public BigDecimal mean() {
        BigDecimal avg = new BigDecimal(0);
        for( int i=0 ;i <this.col.size(); i++){
           avg = avg.add(this.col.get(i));
        }
        avg = avg.divide(new BigDecimal(this.col.size()), 2, RoundingMode. HALF_EVEN);
        this.average =avg;
        avg = avg.setScale(0, RoundingMode.CEILING);

        return BigDecimal.valueOf(avg.doubleValue()).setScale(2, RoundingMode. HALF_EVEN);
    }

    @Override
    public BigDecimal standardDeviation() {
        BigDecimal ans =new BigDecimal( 0);
        BigDecimal sum =new BigDecimal( 0);
        for ( int i=0 ; i<this.col.size();i++) {
            ans = this.col.get(i);
            ans = ans.subtract(average);
            ans = ans.pow(2);
            sum = sum.add(ans);
        }
        this.sumFromStandardDeviation=sum;
        MathContext mc = new MathContext(0);
        sum=sum.divide(new BigDecimal(this.col.size()), 2, RoundingMode.HALF_EVEN);
         sum = sum.setScale(0, RoundingMode.CEILING);
        double d = sum.doubleValue();
        d= Math.sqrt(d);
        return BigDecimal.valueOf(d).setScale(2, RoundingMode.HALF_EVEN);
    }

    @Override
    public BigDecimal variance() {
        int size = this.col.size() ;
        MathContext mc = new MathContext(0);
        BigDecimal ans = sumFromStandardDeviation.divide(new BigDecimal(size), 2, RoundingMode.HALF_EVEN);
        ans = ans.setScale(0, RoundingMode.CEILING);
        return ans.setScale(2);
    }
    @Override
    public BigDecimal median() {
        int sz=this.col.size();
        BigDecimal ans =new BigDecimal(0);
        Collections.sort(col);
        if ((this.col.size()&1 )==1) {
            ans = col.get(sz / 2);

        }else {
            ans = (this.col.get(sz / 2));
            ans =ans.add(this.col.get(sz / 2 - 1));
            ans = ans.divide(BigDecimal.valueOf(2));
        }
        return  ans.setScale(2);
    }
    @Override
    public BigDecimal min() {
        BigDecimal  mn = new BigDecimal("100000000.03");
        for(int i=0 ;i<this.col.size();i++) {
            mn = mn.min(this.col.get(i));
        }
        return mn.setScale(2);
    }

    @Override
    public BigDecimal max() {
        BigDecimal  mx = new BigDecimal("0.03");
        for(int i=0 ;i<this.col.size();i++) {
            mx = mx.max(this.col.get(i));
        }
        return mx.setScale(2);
    }
}
