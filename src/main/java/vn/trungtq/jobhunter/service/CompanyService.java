package vn.trungtq.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.trungtq.jobhunter.domain.Company;
import vn.trungtq.jobhunter.domain.User;
import vn.trungtq.jobhunter.domain.dto.Meta;
import vn.trungtq.jobhunter.domain.dto.ResultPaginationDTO;
import vn.trungtq.jobhunter.repository.CompanyRepository;

import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public void handleDeleteCompany(long id) {
        this.companyRepository.deleteById(id);
    }
    public Company handleGetCompany(long id) {
        return this.companyRepository.findById(id).orElse(null);
    }
    public ResultPaginationDTO handleGetAllCompanies(Specification<Company> spec, Pageable pageable) {
        Page<Company> pageCompanies = companyRepository.findAll(spec,pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotal(pageCompanies.getTotalElements());
        meta.setPages(pageCompanies.getTotalPages());
        rs.setMeta(meta);
        rs.setResult(pageCompanies.getContent());
        return rs;
    }
    public Company handleUpdateCompany(Company company) {
        Company curCompany = handleGetCompany(company.getId());
        if (curCompany != null) {
            curCompany.setName(company.getName());
            curCompany.setAddress(company.getAddress());
            curCompany.setDescription(company.getDescription());
            curCompany.setLogo(company.getLogo());
            return this.companyRepository.save(curCompany);
        }
        return  null;
    }
}
