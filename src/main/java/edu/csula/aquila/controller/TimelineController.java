package edu.csula.aquila.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import edu.csula.aquila.daos.ProposalDao;
import edu.csula.aquila.daos.TimelineDao;
import edu.csula.aquila.model.Form;
import edu.csula.aquila.model.Proposal;
import edu.csula.aquila.model.Timeline;

@RestController
public class TimelineController {

	@Autowired
	private TimelineDao timelineDao;
	
	@Autowired ProposalDao proposalDao;
	

	//create a new timeline
	@RequestMapping(value = "timeline", method = RequestMethod.POST)
	public Timeline newTimeline(@RequestBody Timeline timeline) {
		return timelineDao.saveTimelineForm(timeline);
	}
	
	//update a timeline
	@RequestMapping(value = "/proposal/{proposalId}/timeline/{id}", method = RequestMethod.PUT)
	public Timeline updateTimeline(@RequestBody Timeline timeline, @PathVariable Long id, @PathVariable Long proposalId) {
		 
		//setting up proposal dao to avoid null pointer error when calling a user's intake from the timeline
		Proposal proposal = proposalDao.getProposal(proposalId);
		
		List<Timeline.Stage> stages = timeline.getStages();
		
		//go through each stage
		for(int i = 0; i < stages.size(); i++) {
			//get the map from a stage
			Map<String, Form> forms = stages.get(i).getRequiredForms();
			//go through the map
			for(Map.Entry<String, Form> form: forms.entrySet()) {
				//get the form name
				String key = form.getKey();
				
				switch(key) {
					//if form name is intake form, add the form value
					case "Intake Form" :
						form.setValue(proposal.getIntakeForm());
						System.out.println("intake form linked!");
						break;
					
					//if form name is equipment form, add the form value
					case "Equipment Form" :
						//needs to be related
						System.out.println("equipment form linked!");
						break;
					
					default :
						System.out.println("Invalid Form name");
				}
				

			}
			
			//set the map of forms for the stage
			stages.get(i).setRequiredForms(forms);
		}

		
		//set stages
		timeline.setStages(stages);

		//save timeline
		return timelineDao.saveTimelineForm(timeline);
	}
	
	//return a timeline
	@RequestMapping(value = "timeline/{id}", method = RequestMethod.GET)
	public Timeline getTimeline(@PathVariable Long id) {
		if (timelineDao.getTimelineForm(id).getProposalForm() == null) {
			System.out.println("no proposal");
			
		} else {
			System.out.println("There's a proposal form linked.");
			if(timelineDao.getTimelineForm(id).getProposalForm().getIntakeForm() != null) {
				System.out.println("there's also an intake form linked");
			}
		}
		return timelineDao.getTimelineForm(id);
	}
	
	
	//Get a stage
	@RequestMapping(value = "timeline/stage/{id}", method = RequestMethod.GET)
	public Timeline.Stage getStage(@PathVariable Long id){
		return timelineDao.getStage(id);
	}
	
	//create a stage
	@RequestMapping(value = "timeline/stage/", method = RequestMethod.POST)
	public Timeline.Stage createStage(Timeline.Stage stage){
		return timelineDao.createStage(stage);
	}
	
	//update a stage
	@RequestMapping(value = "timeline/stage/{id}", method = RequestMethod.PUT)
	public Timeline.Stage updateStage(Timeline.Stage stage, @PathVariable Long id){
		return timelineDao.updateStage(stage);
	}
	
	//delete a stage
	@RequestMapping(value = "timeline/stage/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public DeleteResponse deleteStage(Timeline.Stage stage, @PathVariable Long id){
		stage = timelineDao.getStage(id);
		timelineDao.deleteStage(stage);
		return new DeleteResponse("Stage Deleted!");
	}
	
	
	//message to send when a stage is deleted
	public class DeleteResponse{
		private String message;
		
		public DeleteResponse(String message){
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

	}
}



