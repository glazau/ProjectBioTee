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

public class InsertCabecalhoPortalXML implements EventoProgramavelJava {
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
        InsertCabecalho(persistenceEvent);
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
    private void InsertCabecalho (PersistenceEvent event ) throws Exception{
        BigDecimal codTipOper;
        BigDecimal nuNota;
        BigDecimal codParc;
        BigDecimal centroResultado;
        BigDecimal natureza;
        BigDecimal projeto;
        String tipMov;
        Timestamp dhTipOper;
        DynamicVO tgfTop;
        DynamicVO tgfCab;
        JapeSession.SessionHandle hnd = null;

        try{
            hnd = JapeSession.open();
            tgfCab = (DynamicVO) event.getVo();
            if(tgfCab!=null){
                nuNota = tgfCab.asBigDecimalOrZero("NUNOTA");
                codTipOper = tgfCab.asBigDecimalOrZero("CODTIPOPER");
                dhTipOper = tgfCab.asTimestamp("DHTIPOPER");
                codParc = tgfCab.asBigDecimalOrZero("CODPARC");
                tgfTop = getTgfTop(codTipOper,dhTipOper);
                tipMov = tgfTop.asString("TIPMOV");
                    if(tipMov.equals("C")){
                        DynamicVO tgfPar = getTgfPar(codParc);
                        if(tgfPar!=null){
                        natureza = tgfPar.asBigDecimalOrZero("AD_CODNAT");
                        projeto = tgfPar.asBigDecimalOrZero("AD_CODPROJ");
                        centroResultado = tgfPar.asBigDecimalOrZero("AD_CODCENCUS");
                        if (natureza != null && projeto != null && centroResultado != null){
                            JapeWrapper atualizaCabDao = JapeFactory.dao("CabecalhoNota");
                            FluidUpdateVO updateFluidVo = atualizaCabDao.prepareToUpdateByPK(new Object[]{nuNota});
                            updateFluidVo.set("CODNAT",natureza);
                            updateFluidVo.set("CODPROJ",projeto);
                            updateFluidVo.set("CODCENCUS",centroResultado);
                            updateFluidVo.update();
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

    private DynamicVO getTgfTop (BigDecimal codTipOper, Timestamp dhAlter) throws Exception{
        JapeWrapper tgfTopDao = JapeFactory.dao(DynamicEntityNames.TIPO_OPERACAO);
        DynamicVO tgfTopVo = tgfTopDao.findOne("CODTIPOPER= ? AND DHALTER = ?", new Object[]{codTipOper,dhAlter}    );
        return tgfTopVo;
    }
    private DynamicVO getTgfPar (BigDecimal codParc) throws Exception{
        JapeWrapper tgfParDao = JapeFactory.dao(DynamicEntityNames.PARCEIRO);
        DynamicVO tgfParVO = tgfParDao.findOne("CODPARC = ?", new Object[]{codParc});
        return tgfParVO;
    }
}