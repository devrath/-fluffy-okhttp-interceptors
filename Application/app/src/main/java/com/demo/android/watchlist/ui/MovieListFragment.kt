/*
 * Copyright (c) 2020 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.demo.android.watchlist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.demo.android.watchlist.*
import com.demo.android.watchlist.databinding.FragmentMovieListBinding
import com.demo.android.watchlist.model.Movie
import com.demo.android.watchlist.network.ApiProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieListFragment: Fragment() {

  private lateinit var movieAdapter: MovieAdapter

  private var _binding: FragmentMovieListBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    _binding = FragmentMovieListBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    movieAdapter = MovieAdapter()
    binding.movieRecyclerview.adapter = movieAdapter

    val movieApi = ApiProvider.getMovieApi(requireContext())
    movieApi.movies().enqueue(object : Callback<List<Movie>> {
      override fun onFailure(call: Call<List<Movie>>, t: Throwable) {
        showErrorState()
      }

      override fun onResponse(call: Call<List<Movie>>,
                              response: Response<List<Movie>>) {
        if (response.isSuccessful && response.body() != null) {
          val movies = response.body()!!
          if (movies.isEmpty()) {
            showEmptyDataState()
          } else {
            showMovieList(movies)
          }
        } else {
          showErrorState()
        }
      }
    })
  }

  private fun showEmptyDataState() {
    binding.movieRecyclerview.visibility = View.GONE
    binding.progressBar.visibility = View.GONE
    binding.textview.visibility = View.VISIBLE
    binding.textview.text = getString(R.string.there_seems_to_be_no_data)
  }

  private fun showMovieList(movies: List<Movie>) {
    binding.movieRecyclerview.visibility = View.VISIBLE
    binding.progressBar.visibility = View.GONE
    binding.textview.visibility = View.GONE
    movieAdapter.setCharacterList(movies)
  }

  private fun showErrorState() {
    binding.movieRecyclerview.visibility = View.GONE
    binding.progressBar.visibility = View.GONE
    binding.textview.visibility = View.VISIBLE
    binding.textview.text = getString(R.string.something_went_wrong)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}