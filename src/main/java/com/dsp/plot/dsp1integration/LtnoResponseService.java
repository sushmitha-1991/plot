package com.dsp.plot.dsp1integration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.dsp.plot.entity.PlotLtno;
import com.dsp.plot.entity.specification.PlotLtnoSpec;
import com.dsp.plot.repository.PlotLtnoRepository;

@Service
public class LtnoResponseService {

    @Autowired
    private PlotLtnoRepository ltnoRepo;

    public List<LtnoResponseDTO> getAll(String loksabha, String session, LocalDate paperDateFrom, LocalDate paperDateTo,
            String locale, Integer minCodes, String ministryNames, String reportType, String house) {
        // TODO Auto-generated method stub
        List<LtnoResponseDTO> dtoList = new ArrayList<>();
        List<PlotLtno> entitys;
        Specification<PlotLtno> spec = Specification.where(PlotLtnoSpec.isActive(true));
        if (paperDateFrom != null || paperDateTo != null) {
            spec = spec.and(PlotLtnoSpec.hasDateBetween(paperDateFrom, paperDateTo));
        }
        if (reportType != null) {
            // spec = spec.and(PlotLtnoSpec.hasPaperType(reportType));
            spec = spec.and(PlotLtnoSpec.hasPaperName(reportType));

        }
        System.out.println(house);
        if("LS".equalsIgnoreCase(house))
        {
            spec=spec.and(PlotLtnoSpec.hasRajyaSabhaSessionNoNULL());

       
        }
        if("RS".equalsIgnoreCase(house))
        {
            spec=spec.and(PlotLtnoSpec.hasLokSabhaNoNULL());
            spec=spec.and(PlotLtnoSpec.hasLokSabhaSessionNoNULL());
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "ltnoDate");
        entitys = ltnoRepo.findAll(spec, sort);
        // entitys = ltnoRepo.findAll(spec);

        System.out.println("entity " + entitys);
        for (PlotLtno ltno : entitys) {
            Boolean addInResponse = true;

            addInResponse = isInLsAndRs(house, loksabha, session, ltno);
            LtnoResponseDTO dto = mapToDTO(ltno, house);
            // System.out.println("min code "+minCodes);
            if (minCodes != null) {
                addInResponse = compareIntegerValues(minCodes, dto.getMinistryCode());
                System.out.println("add in resp if ministry code" + addInResponse);
            }
            if (ministryNames != null) {
                addInResponse = compareValues(ministryNames.trim(), dto.getMinistry().trim());
                System.out.println("add in resp if ministry code" + addInResponse);
            }
            if (addInResponse)
                dtoList.add(dto);
        }
        return dtoList;
    }

    private Boolean compareIntegerValues(Integer minCodes, Integer ministryCode) {
        // TODO Auto-generated method stub
        if (minCodes.equals(ministryCode))
            return true;
        else
            return false;
    }

    private Boolean isInLsAndRs(String house, String loksabha, String session, PlotLtno ltno) {
        if (house == null)
            return true;
        //     System.out.println("loksabha");

        // System.out.println(loksabha);
        switch (house.toUpperCase()) {
            case "LS":
            System.out.println("LS");
                boolean lsMatch = true;
                if (loksabha != null) {
                    lsMatch &= compareValues(loksabha, ltno.getLokSabhaSession());
                }
                if (session != null) {
                    lsMatch &= compareValues(session, ltno.getLokSabhaSessionNumber());
                }
                System.out.println("ls match "+lsMatch);
                return lsMatch;

            case "RS":
            System.out.println("RS");
                return session == null || compareValues(session, ltno.getRajyaSabhaSession());
            default:
                return false;
        }
    }

    private Boolean compareValues(String value1, String value2) {
        if (value1.equalsIgnoreCase(value2))
            return true;
        else
            return false;
    }

    private LtnoResponseDTO mapToDTO(PlotLtno entity, String house) {
        String session = "";
        String lsNo="";
        System.out.println("entity "+entity);
        if ("LS".equalsIgnoreCase(house) && entity.getLokSabhSessionDetails()!=null)
        {
            lsNo = entity.getLokSabhSessionDetails().getLokSabha().getLokSabhaNumber();
                session = entity.getLokSabhSessionDetails().getSessionNumber();
        }
        else if ("RS".equalsIgnoreCase(house) && entity.getRajyaSabhaSessionDetails()!=null)
        
            session = entity.getRajyaSabhaSessionDetails().getSessionNumber();

        String ministryName = "";
        Integer ministryCode = null;
        if (entity.getMinistries() != null) {
            ministryName = entity.getMinistries().getMinistryName();
            ministryCode = entity.getMinistries().getMinistryCode();
        }
        return LtnoResponseDTO.builder()
                .title(entity.getTitle())
                .date(dateFormatToString(entity.getLtnoDate()))
                .loksabha(lsNo)
                .ministry(ministryName)
                .ministryCode(ministryCode)
                .reportType(entity.getPaperType())
                .session(session)
                .build();
    }

    private String dateFormatToString(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        String formattedDate = date.format(formatter);
        return formattedDate;
    }

}