/*
 * Name: <your name>
 * EID: <your EID>
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Your solution goes in this class.
 *
 * Please do not modify the other files we have provided for you, as we will use
 * our own versions of those files when grading your project. You are
 * responsible for ensuring that your solution works with the original version
 * of all the other files we have provided for you.
 *
 * That said, please feel free to add additional files and classes to your
 * solution, as you see fit. We will use ALL of your additional files when
 * grading your solution.
 */
public class Program1 extends AbstractProgram1 {


    /**
     * Determines whether a candidate Matching represents a solution to the stable matching problem.
     * Study the description of a Matching in the project documentation to help you with this.
     */
    /*
        * Check for type one instability
    For each high school h 
        For each student s’ that is unmatched
        Check if the high school would rather have s’ than one of their current students
        if so, declare unstable

        Check for type two instability

    For each high school h 
        For each student s’ that’s already matched
            Check if the high school would rather have s’ than one of their current students
                If so, check if the student would rather have h than h’, their current high school
                    if so, declare unstable
            otherwise do nothing
        


     * 
     */

    @Override
public boolean isStableMatching(Matching problem) {
    int m = problem.getHighSchoolCount();
    int n = problem.getStudentCount();

    ArrayList<ArrayList<Integer>> highschoolPrefs = problem.getHighSchoolPreference();//indexed by high school, contains an arraylist of their student pref, indexed by student
    ArrayList<ArrayList<Integer>> studentPrefs = problem.getStudentPreference();//indexed by student, contains an arraylist of their high school pref, indexed by high school
    ArrayList<Integer> currMatching = problem.getStudentMatching();
    ArrayList<Integer> highschoolSpots = problem.getHighSchoolSpots();

for(int sp =0;sp<n;sp++){//For each student sp

        for(int hp =0; hp < m;hp++){//for each high school to consider


            int leastPreferredStudent = -1;
            int leastPreferredRank = -1;
            int studentsInSchool = 0;

            for(int s =0; s < n;s++){//find every student that belongs to that school
                        if(currMatching.get(s) == hp){ //check for belonging to that school
                        studentsInSchool++;
                            if(leastPreferredRank < highschoolPrefs.get(hp).indexOf(s)){ //compare to find least desirable student
                                //drop s, add studentprime  
                                leastPreferredRank = highschoolPrefs.get(hp).indexOf(s);
                                leastPreferredStudent = s;
                            }
                        }
                    }
            
            int schoolSpots = highschoolSpots.get(hp);
            if(studentsInSchool < schoolSpots){ //something has gone horribly wrong if there are any high schools without all their spots filled
                System.out.println("High School Has Empty Spot");
                return false;
            }

            int s = leastPreferredStudent;
            if(compareStudents(sp, hp, s, highschoolPrefs)){//if sp is more desirable than s
                if(currMatching.get(sp)==-1){//If student is unmatched (check for type 1)
                    System.out.println("type one instability");
                    return false; //then it's unstable
                }else{//if student is matched, check for type 2
                    int h = currMatching.get(s); //h is sps current school
                    if(compareSchools(hp, h, s, studentPrefs)){//if hp is more desirable than h
                        System.out.println("type two instability");
                        return false; 
                    }
                }
            }
    }
}
    return true;
}

//returns true if hp is more desirable than h
private boolean compareSchools(int hp,int h, int s, ArrayList<ArrayList<Integer>> studentPreferences){
    if(studentPreferences.get(s).indexOf(hp) < studentPreferences.get(s).indexOf(h)){//if index of hp is lower than index of h, hp is more desirable
        return true;
    }
    return false;
}

//returns true if sp is more desirable than s
private boolean compareStudents(int sp,int h, int s, ArrayList<ArrayList<Integer>> schoolPreferences){
    if(schoolPreferences.get(h).indexOf(sp) < schoolPreferences.get(h).indexOf(s)){//if index of sp is lower than index of s, sp is more desirable
        return true;
    }
    return false;
}



private int[][] createSchoolRankMatrix(ArrayList<ArrayList<Integer>> highschoolPrefs ,int m, int n) {
    int[][] matrix = new int[m][n]; //m schools rows, n students columns
    for(int school = 0; school <m;school++){
        for(int rank =0; rank < n; rank++){
            matrix[school][highschoolPrefs.get(school).get(rank)] = rank;
        }
    }

    return matrix;
}




        /**

     * Determines a solution to the stable matching problem from the given input set. Study the
     * project description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */
    @Override
    public Matching stableMatchingGaleShapley_studentoptimal(Matching problem) {


        int m = problem.getHighSchoolCount();
        int n = problem.getStudentCount();

        ArrayList<ArrayList<Integer>> highschoolPrefs = new ArrayList<>(problem.getHighSchoolPreference());
        
        ArrayList<ArrayList<Integer>> studentPrefs = new ArrayList<>(problem.getStudentPreference());

        ArrayList<Integer> highschoolSpots = new ArrayList<>();
        highschoolSpots.addAll(problem.getHighSchoolSpots());//indexed by high schools, contains there # of spots

        ArrayList<Integer> studentMatching = new ArrayList<>(Collections.nCopies(n, -1)); // Initialize all students as unmatched
        ArrayList<Integer> studentPreferenceIndex = new ArrayList<>(Collections.nCopies(n, 0)); // Initialize all students as unmatched
        int[][] matrix = createSchoolRankMatrix(highschoolPrefs,m,n); //create a 2d array indexed [school][student], value is rank of the student for each school
        
        LinkedList<LinkedList<Integer>> acceptedStudentsNames = new LinkedList<>();


        // Initialize the 2D ArrayList to be empty
        for (int i = 0; i < m; i++) {
            acceptedStudentsNames.add(new LinkedList<>());
        }

        boolean studentRemains = true;
        int studentPrime = -1;
        while(studentRemains){ //while a student hasn't reached the end of their preferencelist. # of elements in preference lists = m * n

            studentRemains = false;

            for(int sp =0;sp < n;sp++){ //search for an unmatched student that hasn't reached the end of their preference list
                if(studentPreferenceIndex.get(sp) < m) //check that hasn't reached end of preference list 
                if(studentMatching.get(sp) == -1){ //if that student is unmatched
                    //have that student propose to the next school on their list
                    studentRemains = true;
                    studentPrime = sp;
                    break;
                }
            }

            if(studentRemains){
                int h = studentPrefs.get(studentPrime).get(studentPreferenceIndex.get(studentPrime)); //gets next desired school
                studentPreferenceIndex.set(studentPrime, studentPreferenceIndex.get(studentPrime) +1); //increment after use
                int studentPrimeRank = matrix[h][studentPrime];

                LinkedList<Integer> currAcceptNames= acceptedStudentsNames.get(h);
                ListIterator<Integer> nameIterator = currAcceptNames.listIterator();

                if(highschoolSpots.get(h) > 0 ){//check if the school has an open spot
                    highschoolSpots.set(h,highschoolSpots.get(h) - 1); //decrement number of spots
                    studentMatching.set(studentPrime, h); //match the two

                    

                    //insert in an ordered way, 0 index least desirable
                    

                    
                    // Find the correct spot to insert the new number

                    if(currAcceptNames.isEmpty()){
                        currAcceptNames.add(nameIterator.nextIndex(), studentPrime);
                    }else{
                        while (nameIterator.hasNext()) {
                            int nextName = nameIterator.next();
                            if (matrix[h][studentPrimeRank] > matrix[h][nextName]) {
                                // Insert the new number at the correct spot
                                nameIterator.add(studentPrime);
                                break;
                            }
                        }
                    }

                    

                    
                }else{ //check if the high school would rather have them or their least desirable student
                    //find least desired student

                        //check if it has at least one spot
                        if(!currAcceptNames.isEmpty()){
                            int leastDesirable = currAcceptNames.get(0);
                            int leastDesirableRank =matrix[h][leastDesirable]; 
                            int studentRank = matrix[h][studentPrime]; 

                            if( studentRank < leastDesirableRank  ){//check if studentPrime is more desirable than that schools least desirable student, index 0
                                //perform swap
                                studentMatching.set(studentPrime, h);
                                studentMatching.set(currAcceptNames.get(0), -1); //make undesirable single
                                //remove from currAcceptNames. It's just index 0 lol
                                currAcceptNames.remove(0);
                                
                                // Find the correct spot to insert the new number
            

                                if(currAcceptNames.isEmpty()){
                                    currAcceptNames.add(nameIterator.nextIndex(), studentPrime);
                                }else{
                                    while (nameIterator.hasNext()) {
                                        int nextName = nameIterator.next();
                                        if (matrix[h][studentPrimeRank] > matrix[h][nextName]) {
                                            // Insert the new number at the correct spot
                                            nameIterator.add(studentPrime);
                                            break;
                                        }
                                    }

                                }

                                
                            }

                        }

                }
        }
            }

            problem.setStudentMatching(studentMatching);
            return problem;
    }

























    /**
     * Determines a solution to the stable matching problem from the given input set. Study the
     * project description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */
    @Override
    public Matching stableMatchingGaleShapley_highschooloptimal(Matching problem) {
        int m = problem.getHighSchoolCount();
        int n = problem.getStudentCount();
        int totalSpots = problem.totalHighSchoolSpots();

        ArrayList<ArrayList<Integer>> highschoolPrefs = problem.getHighSchoolPreference();//indexed by high school, contains an arraylist of their student pref, indexed by student
        ArrayList<ArrayList<Integer>> studentPrefs = problem.getStudentPreference();//indexed by student, contains an arraylist of their high school pref, indexed by high school
        ArrayList<Integer> highschoolSpots = new ArrayList<>();
        highschoolSpots.addAll(problem.getHighSchoolSpots());//indexed by high schools, contains there # of spots
        ArrayList<Integer> schoolPreferenceIndex = new ArrayList<>(Collections.nCopies(m, 0)); // Initialize all students as unmatched
        ArrayList<Integer> studentMatching = new ArrayList<>(Collections.nCopies(n, -1)); // Initialize all students as unmatched

        while(totalSpots >0){ //go until no spots remain for any school

            int currentSchool = -1;
            for(int h =0; h < m; h++){ // find a high school that still has spots
                if(highschoolSpots.get(h) > 0){
                    currentSchool = h;
                    break;
                }
            }

            int studentPrime = highschoolPrefs.get(currentSchool).get(schoolPreferenceIndex.get(currentSchool)); //find the next desired student for that high school
            schoolPreferenceIndex.set(currentSchool,schoolPreferenceIndex.get(currentSchool) + 1); //need to increment index 

            if(studentMatching.get(studentPrime) == -1){//if available, matches immediately
                highschoolSpots.set(currentSchool,highschoolSpots.get(currentSchool) -1); //decrement number of spots for that hs
                totalSpots --; //decrement total number of spots
                studentMatching.set(studentPrime,currentSchool); //actually perform matching
            }else{ 

                //check if that student would prefer the high school to the one they're currently matched to
                int AlreadyMatchedSchool = studentMatching.get(studentPrime);
                if( studentPrefs.get(studentPrime).indexOf(currentSchool) < studentPrefs.get(studentPrime).indexOf(AlreadyMatchedSchool)){
                    highschoolSpots.set(AlreadyMatchedSchool,highschoolSpots.get(AlreadyMatchedSchool) +1); //open up spot at sniped hs
                    highschoolSpots.set(currentSchool,highschoolSpots.get(currentSchool) -1); //decrement number of spots for current hs
                    studentMatching.set(studentPrime,currentSchool); //actually perform matching
                }

            }
            


        }

        

         problem.setStudentMatching(studentMatching);
            return problem;
    }









}