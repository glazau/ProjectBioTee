package br.com.biotee.glaycon.events;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;
import br.com.sankhya.modelcore.util.DynamicEntityNames;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class InsertFinanceiroPortalXML implements EventoProgramavelJava {
    @Override
    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {
        InsertFinanceciro(persistenceEvent);
    }

    @Override
    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext transactionContext) throws Exception {

    }

    private void InsertFinanceciro (PersistenceEvent persistenceEvent) throws Exception {
        JapeSession.SessionHandle hnd = null;
        String origem;
        BigDecimal codEmp;
        BigDecimal codTipOper;
        BigDecimal banco;
        BigDecimal nufin;
        BigDecimal conta;
        String tipMov;
        Timestamp dhTipOper;
        DynamicVO tgfTop;
        DynamicVO tsiEmp;

        try {
            hnd = JapeSession.open();
            DynamicVO tgfFin = (DynamicVO) persistenceEvent.getVo();
            if(tgfFin!=null){
                codTipOper = tgfFin.asBigDecimalOrZero("CODTIPOPER");
                dhTipOper = tgfFin.asTimestamp("DHTIPOPER");
                origem = tgfFin.asString("ORIGEM");
                nufin = tgfFin.asBigDecimalOrZero("NUFIN");

                codEmp = tgfFin.asBigDecimalOrZero("CODEMP");

                tgfTop = getTgfTop(codTipOper,dhTipOper);
                tipMov = tgfTop.asString("TIPMOV");

                tsiEmp = getTsiEmp(codEmp);
                banco = tsiEmp.asBigDecimalOrZero("AD_CODBCO");
                conta = tsiEmp.asBigDecimalOrZero("AD_CODCTABCOINT");

                if(tgfTop != null && tgfTop!=null){
                    if(tipMov.equals("C") && origem.equals("E")){
                        if(banco != null && conta != null) {
                            JapeWrapper financeiroDao = JapeFactory.dao(DynamicEntityNames.FINANCEIRO);
                            FluidUpdateVO financeiroFluidVo = financeiroDao.prepareToUpdateByPK(nufin);
                            financeiroFluidVo.set("CODBCO", banco);
                            financeiroFluidVo.set("CODCTABCOINT", conta);
                            financeiroFluidVo.update();
                        }
                    }
                }
            }
        } catch (Exception e){
            System.out.println(e);
        } finally {
            JapeSession.close(hnd);
        }
    }

    private DynamicVO getTsiEmp (BigDecimal codemp) throws Exception {
        JapeWrapper tsiEmpDAO = JapeFactory.dao("Empresa");
        DynamicVO tsiEmpVO = tsiEmpDAO.findOne("CODEMP = ?", new Object[]{codemp});
        return tsiEmpVO;
    }
    private DynamicVO getTgfTop (BigDecimal codTipOper, Timestamp dhAlter) throws Exception{
        JapeWrapper tgfTopDao = JapeFactory.dao(DynamicEntityNames.TIPO_OPERACAO);
        DynamicVO tgfTopVo = tgfTopDao.findOne("CODTIPOPER= ? AND DHALTER = ?", new Object[]{codTipOper,dhAlter});
        return tgfTopVo;
    }

}
