package edu.csula.aquila.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.csula.aquila.daos.DummyDao;
import edu.csula.aquila.daos.ProposalDao;
import edu.csula.aquila.daos.UserDao;
import edu.csula.aquila.model.DummyForm;
import edu.csula.aquila.model.Proposal;
import edu.csula.aquila.model.User;

@RestController
public class ProposalController {

	@Autowired
	private ProposalDao proposalDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private DummyDao dummyDao;
	
	@RequestMapping(value = "api/proposal", method = RequestMethod.GET)
	public Proposal getProposal(@RequestParam Long id){
		return proposalDao.getProposal(id);
	}
	
	@RequestMapping(value = "api/proposal", method = RequestMethod.POST)
	public Proposal newProposal(@RequestBody ProposalInstantiate proposalInstantiate) {
		//create proposal and set the name
		Proposal proposal = new Proposal();
		
		
		proposal.setProposalName(proposalInstantiate.getProposalName());
		
		//set the user
		User user = userDao.getUser(proposalInstantiate.getUserId());
		proposal.setUser(user);
		
		//set the status
		proposal.setStatus("Draft");

		//set the date
		proposal.setDateCreated(new java.sql.Date(Calendar.getInstance().getTime().getTime()));
		
		//create a null intake form
		DummyForm dummyForm = new DummyForm();
		dummyForm.setProjectTitle(proposalInstantiate.getProposalName());
		dummyForm.setPrincipleInvestigator(user.getFirstName() + " " + user.getLastName());
		dummyDao.saveDummyForm(dummyForm);
		proposal.setDummyForm(dummyForm);
		
		return proposal = proposalDao.saveProposal(proposal);
	}
	
	@RequestMapping(value = "api/getproposals", method = RequestMethod.GET)
	public List<Proposal> getProposalsOfUser(@RequestParam Long id){
		
		return proposalDao.getProposalsOfUser(id);
	}
	
	
}

class ProposalInstantiate{
	String proposalName;
	Long userId;
	
	ProposalInstantiate(){
		
	}
	
	public String getProposalName() {
		return proposalName;
	}
	
	public Long getUserId() {
		return userId;
	}
	
}

